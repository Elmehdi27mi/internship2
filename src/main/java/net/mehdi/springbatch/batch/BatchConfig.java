package net.mehdi.springbatch.batch;

import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import net.mehdi.springbatch.ssh.SSHService;
import net.mehdi.springbatch.batch.listener.CustomStepExecutionListener;
import net.mehdi.springbatch.dto.IctEncoursBrutDto;
import net.mehdi.springbatch.entities.IctEncoursBrut;
import net.mehdi.springbatch.integration.IntegrationConfig;
import net.mehdi.springbatch.mappers.IctEncoursBrutMapper;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@Configuration
@EnableBatchProcessing
public class BatchConfig {

    public static final String DEFAULT_FILE_PATH = "file.xml";

    @Value("${application.batch.chunkSize}")
    private Integer chunkSize;

    @Bean
    public JobLauncher jobLauncher(JobRepository jobRepository) throws Exception {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    @Bean
    public Job dailyJob(EntityManagerFactory entityManagerFactory, JobRepository jobRepository, PlatformTransactionManager transactionManager, IntegrationConfig.CustomGateway customGateway, SSHService sshService) {
        return new JobBuilder("dailyJob", jobRepository)
                .start(databaseToFileStep(entityManagerFactory, jobRepository, transactionManager))
                .next(modifyFileStep(jobRepository, transactionManager))  // Step to modify XML file
                .next(fileToSftpStep(jobRepository, transactionManager, customGateway))
                .next(executeSSHCommandStep(jobRepository, transactionManager, sshService))
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        log.info("Before Job: {}", jobExecution);
                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        log.info("After Job: {}", jobExecution);
                    }
                })
                .build();
    }

    @Bean
    public Step databaseToFileStep(EntityManagerFactory entityManagerFactory, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("databaseToFileStep", jobRepository)
                .<IctEncoursBrut, IctEncoursBrutDto>chunk(chunkSize, transactionManager)
                .listener(new CustomStepExecutionListener())
                .reader(itemReader(entityManagerFactory))
                .processor(itemProcessor(null))
                .writer(itemWriter())
                .allowStartIfComplete(true)
                .build();
    }

    // New step to remove <root> and </root> tags from the generated XML
    @Bean
    public Step modifyFileStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("modifyFileStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    String filePath = DEFAULT_FILE_PATH;
                    try {
                        removeRootTags(filePath);  // Remove root tags from the XML file
                    } catch (IOException e) {
                        log.error("Failed to modify the XML file", e);
                        throw new RuntimeException("Failed to modify the XML file", e);
                    }
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Step fileToSftpStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, IntegrationConfig.CustomGateway sftpGateway) {
        return new StepBuilder("fileToSftpStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    try {
                        sftpGateway.sendToSftp(new FileSystemResource(DEFAULT_FILE_PATH).getFile());
                    } catch (Exception e) {
                        log.error("Failed to send file to SFTP", e);
                        throw new RuntimeException("SFTP file transfer failed", e);
                    }
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Step executeSSHCommandStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, SSHService sshService) {
        return new StepBuilder("executeSSHCommandStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    String command = "/home/mehdi/script.sh";  // Define the script or command to be executed
                    String result = sshService.executeCommand("bash " + command);
                    log.info("SSH Command Output: {}", result);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public ItemProcessor<IctEncoursBrut, IctEncoursBrutDto> itemProcessor(IctEncoursBrutMapper mapper) {
        return mapper::entityToDto;
    }

    @Bean
    public JpaPagingItemReader<IctEncoursBrut> itemReader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<IctEncoursBrut>()
                .name("ictEncoursBrutReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT e FROM IctEncoursBrut e")
                .pageSize(chunkSize)
                .build();
    }

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(IctEncoursBrutDto.class);
        return marshaller;
    }

    @Bean
    public StaxEventItemWriter<IctEncoursBrutDto> itemWriter() {
        return new StaxEventItemWriterBuilder<IctEncoursBrutDto>()
                .name("xmlItemWriter")
                .resource(new FileSystemResource(DEFAULT_FILE_PATH))
                .marshaller(marshaller())
                .build();
    }

    // Method to remove <root> and </root> tags from the file
    public void removeRootTags(String filePath) throws IOException {
        // Read file content as a string
        String content = new String(Files.readAllBytes(Paths.get(filePath)));

        // Remove <root> and </root> tags
        content = content.replaceAll("<root>", "").replaceAll("</root>", "");

        // Write the modified content back to the file
        Files.write(Paths.get(filePath), content.getBytes());
    }
}




/*package net.mehdi.springbatch.batch;

import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import net.mehdi.springbatch.ssh.SSHService;
import net.mehdi.springbatch.batch.listener.CustomStepExecutionListener;
import net.mehdi.springbatch.dto.IctEncoursBrutDto;
import net.mehdi.springbatch.entities.IctEncoursBrut;
import net.mehdi.springbatch.integration.IntegrationConfig;
import net.mehdi.springbatch.mappers.IctEncoursBrutMapper;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@EnableBatchProcessing
public class BatchConfig {

    public static final String DEFAULT_FILE_PATH = "file.xml";

    @Value("${application.batch.chunkSize}")
    private Integer chunkSize;

    @Bean
    public JobLauncher jobLauncher(JobRepository jobRepository) throws Exception {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    @Bean
    public Job dailyJob(EntityManagerFactory entityManagerFactory, JobRepository jobRepository, PlatformTransactionManager transactionManager, IntegrationConfig.CustomGateway customGateway, SSHService sshService) {
        return new JobBuilder("dailyJob", jobRepository)
                .start(databaseToFileStep(entityManagerFactory, jobRepository, transactionManager))
                .next(fileToSftpStep(jobRepository, transactionManager, customGateway))
                .next(executeSSHCommandStep(jobRepository, transactionManager, sshService))  // Ajouter l'étape SSH
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        log.info("Before Job: {}", jobExecution);
                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        log.info("After Job: {}", jobExecution);
                    }
                })
                .build();
    }

    @Bean
    public Step databaseToFileStep(EntityManagerFactory entityManagerFactory, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("databaseToFileStep", jobRepository)
                .<IctEncoursBrut, IctEncoursBrutDto>chunk(chunkSize, transactionManager)
                .listener(new CustomStepExecutionListener())
                .reader(itemReader(entityManagerFactory))
                .processor(itemProcessor(null))
                .writer(itemWriter())
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Step fileToSftpStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, IntegrationConfig.CustomGateway sftpGateway) {
        return new StepBuilder("fileToSftpStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    try {
                        sftpGateway.sendToSftp(new FileSystemResource(DEFAULT_FILE_PATH).getFile());
                    } catch (Exception e) {
                        log.error("Failed to send file to SFTP", e);
                        throw new RuntimeException("SFTP file transfer failed", e);
                    }
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .allowStartIfComplete(true)
                .build();
    }

    // Nouveau Step pour exécuter une commande SSH
    @Bean
    public Step executeSSHCommandStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, SSHService sshService) {
        return new StepBuilder("executeSSHCommandStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    // Exécution d'un script ou commande via SSH
                    String command = "/home/mehdi/script.sh"; // ou toute autre commande
                    String result = sshService.executeCommand("bash " + command);
                    log.info("SSH Command Output: {}", result);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public ItemProcessor<IctEncoursBrut, IctEncoursBrutDto> itemProcessor(IctEncoursBrutMapper mapper) {
        return mapper::entityToDto;
    }

    @Bean
    public JpaPagingItemReader<IctEncoursBrut> itemReader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<IctEncoursBrut>()
                .name("ictEncoursBrutReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT e FROM IctEncoursBrut e")
                .pageSize(chunkSize)
                .build();
    }

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(IctEncoursBrutDto.class);
        return marshaller;
    }

    @Bean
    public StaxEventItemWriter<IctEncoursBrutDto> itemWriter() {
        return new StaxEventItemWriterBuilder<IctEncoursBrutDto>()
                .name("xmlItemWriter")
                .resource(new FileSystemResource(DEFAULT_FILE_PATH))
                .rootTagName("IctEncoursBruts")
                .marshaller(marshaller())
                .build();
    }
}
*/



























/*package net.mehdi.springbatch.batch;

import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import net.mehdi.springbatch.ssh.SSHService;
import net.mehdi.springbatch.batch.listener.CustomStepExecutionListener;
import net.mehdi.springbatch.dto.IctEncoursBrutDto;
import net.mehdi.springbatch.entities.IctEncoursBrut;
import net.mehdi.springbatch.integration.IntegrationConfig;
import net.mehdi.springbatch.mappers.IctEncoursBrutMapper;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@EnableBatchProcessing
public class BatchConfig {

    public static final String DEFAULT_FILE_PATH = "file.xml";

    @Value("${application.batch.chunkSize}")
    private Integer chunkSize;

    @Bean
    public JobLauncher jobLauncher(JobRepository jobRepository) throws Exception {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    @Bean
    public Job dailyJob(EntityManagerFactory entityManagerFactory, JobRepository jobRepository, PlatformTransactionManager transactionManager, IntegrationConfig.CustomGateway customGateway, SSHService sshService) {
        return new JobBuilder("dailyJob", jobRepository)
                .start(databaseToFileStep(entityManagerFactory, jobRepository, transactionManager))
                .next(fileToSftpStep(jobRepository, transactionManager, customGateway))
                .next(executeSSHCommandStep(jobRepository, transactionManager, sshService))  // Ajouter l'étape SSH
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        log.info("Before Job: {}", jobExecution);
                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        log.info("After Job: {}", jobExecution);
                    }
                })
                .build();
    }

    @Bean
    public Step databaseToFileStep(EntityManagerFactory entityManagerFactory, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("databaseToFileStep", jobRepository)
                .<IctEncoursBrut, IctEncoursBrutDto>chunk(chunkSize, transactionManager)
                .listener(new CustomStepExecutionListener())
                .reader(itemReader(entityManagerFactory))
                .processor(itemProcessor(null))
                .writer(itemWriter())
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Step fileToSftpStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, IntegrationConfig.CustomGateway sftpGateway) {
        return new StepBuilder("fileToSftpStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    try {
                        sftpGateway.sendToSftp(new FileSystemResource(DEFAULT_FILE_PATH).getFile());
                    } catch (Exception e) {
                        log.error("Failed to send file to SFTP", e);
                        throw new RuntimeException("SFTP file transfer failed", e);
                    }
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .allowStartIfComplete(true)
                .build();
    }

    // Nouveau Step pour exécuter une commande SSH
    @Bean
    public Step executeSSHCommandStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, SSHService sshService) {
        return new StepBuilder("executeSSHCommandStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    // Exécution d'un script ou commande via SSH
                    String command = "/home/mehdi/script.sh"; // ou toute autre commande
                    String result = sshService.executeCommand("bash " + command);
                    log.info("SSH Command Output: {}", result);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public ItemProcessor<IctEncoursBrut, IctEncoursBrutDto> itemProcessor(IctEncoursBrutMapper mapper) {
        return mapper::entityToDto;
    }

    @Bean
    public JpaPagingItemReader<IctEncoursBrut> itemReader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<IctEncoursBrut>()
                .name("ictEncoursBrutReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT e FROM IctEncoursBrut e")
                .pageSize(chunkSize)
                .build();
    }

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(IctEncoursBrutDto.class);
        return marshaller;
    }

    @Bean
    public StaxEventItemWriter<IctEncoursBrutDto> itemWriter() {
        return new StaxEventItemWriterBuilder<IctEncoursBrutDto>()
                .name("xmlItemWriter")
                .resource(new FileSystemResource(DEFAULT_FILE_PATH))
                .rootTagName("IctEncoursBruts")
                .marshaller(marshaller())
                .build();
    }
}
*/



































/*package net.mehdi.springbatch.batch;

import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;

import net.mehdi.springbatch.batch.listener.CustomStepExecutionListener;
import net.mehdi.springbatch.dto.IctEncoursBrutDto;
import net.mehdi.springbatch.entities.IctEncoursBrut;
import net.mehdi.springbatch.integration.IntegrationConfig;
import net.mehdi.springbatch.mappers.IctEncoursBrutMapper;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@EnableBatchProcessing
public class BatchConfig {

    public static final String DEFAULT_FILE_PATH = "file.xml";

    @Value("${application.batch.chunkSize}")
    private Integer chunkSize ;

    @Bean
    public JobLauncher jobLauncher(JobRepository jobRepository) throws Exception {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    @Bean
    public Job dailyJob(EntityManagerFactory entityManagerFactory, JobRepository jobRepository, PlatformTransactionManager transactionManager, IntegrationConfig.CustomGateway customGateway) {
        return new JobBuilder("dailyJob", jobRepository)
                .start(databaseToFileStep(entityManagerFactory, jobRepository, transactionManager))
                .next(fileToSftpStep(jobRepository, transactionManager, customGateway))
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        log.info("Before Job: {}", jobExecution);
                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        log.info("After Job: {}", jobExecution);
                    }
                })
                .build();
    }

    @Bean
    public Step databaseToFileStep(EntityManagerFactory entityManagerFactory, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("databaseToFileStep", jobRepository)
                .<IctEncoursBrut, IctEncoursBrutDto>chunk(chunkSize, transactionManager)
                .listener(new CustomStepExecutionListener())
                .reader(itemReader(entityManagerFactory))
                .processor(itemProcessor(null))
                .writer(itemWriter())
                .allowStartIfComplete(true)
                .build();
    }



    @Bean
    public Step fileToSftpStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, IntegrationConfig.CustomGateway sftpGateway) {
        return new StepBuilder("fileToSftpStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    try {
                        sftpGateway.sendToSftp(new FileSystemResource(DEFAULT_FILE_PATH).getFile());
                    } catch (Exception e) {
                        log.error("Failed to send file to SFTP", e);
                        throw new RuntimeException("SFTP file transfer failed", e);
                    }
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .allowStartIfComplete(true)
                .build();
    }
    @Bean
    public ItemProcessor<IctEncoursBrut, IctEncoursBrutDto> itemProcessor(IctEncoursBrutMapper mapper) {
        return mapper::entityToDto;
    }

    @Bean
    public JpaPagingItemReader<IctEncoursBrut> itemReader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<IctEncoursBrut>()
                .name("ictEncoursBrutReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT e FROM IctEncoursBrut e")
                .pageSize(chunkSize)
                .build();
    }

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(IctEncoursBrutDto.class);
        return marshaller;
    }

    @Bean
    public StaxEventItemWriter<IctEncoursBrutDto> itemWriter() {
        return new StaxEventItemWriterBuilder<IctEncoursBrutDto>()
                .name("xmlItemWriter")
                .resource(new FileSystemResource(DEFAULT_FILE_PATH))
                .rootTagName("IctEncoursBruts")
                .marshaller(marshaller())
                .build();
    }

}*/








//package net.mehdi.springbatch.batch;
//
//import jakarta.persistence.EntityManagerFactory;
//import lombok.extern.slf4j.Slf4j;
//import net.mehdi.springbatch.batch.listener.CustomStepExecutionListener;
//import net.mehdi.springbatch.dto.IctEncoursBrutDto;
//import net.mehdi.springbatch.entities.IctEncoursBrut;
//import net.mehdi.springbatch.integration.IntegrationConfig;
//import net.mehdi.springbatch.mappers.IctEncoursBrutMapper;
//import org.springframework.batch.core.*;
//import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
//import org.springframework.batch.core.configuration.annotation.StepScope;
//import org.springframework.batch.core.job.builder.JobBuilder;
//import org.springframework.batch.core.partition.support.Partitioner;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.step.builder.StepBuilder;
//import org.springframework.batch.core.partition.PartitionHandler;
//import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
//import org.springframework.batch.item.ExecutionContext;
//import org.springframework.batch.item.ItemProcessor;
//import org.springframework.batch.item.database.JpaPagingItemReader;
//import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
//import org.springframework.batch.item.xml.StaxEventItemWriter;
//import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
//import org.springframework.batch.repeat.RepeatStatus;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.FileSystemResource;
//import org.springframework.core.task.SimpleAsyncTaskExecutor;
//import org.springframework.oxm.jaxb.Jaxb2Marshaller;
//import org.springframework.transaction.PlatformTransactionManager;
//
//import java.io.File;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.atomic.AtomicInteger;
//
//@Slf4j
//@Configuration
//@EnableBatchProcessing
//public class BatchConfig {
//
//    @Value("${application.batch.chunkSize}")
//    private Integer chunkSize;
//
//    @Value("${application.batch.recordsPerFile}")
//    private Integer recordsPerFile;
//
//    @Bean
//    public Job partitionedJob(EntityManagerFactory entityManagerFactory, JobRepository jobRepository, PlatformTransactionManager transactionManager, IntegrationConfig.CustomGateway customGateway) {
//        return new JobBuilder("partitionedJob", jobRepository)
//                .start(masterStep(jobRepository, transactionManager, partitionHandler(slaveStep(jobRepository, transactionManager, entityManagerFactory), jobRepository, transactionManager)))
//                .next(fileToSftpStep(jobRepository, transactionManager, customGateway)) // Envoi des fichiers après génération
//                .listener(jobExecutionListener())
//                .build();
//    }
//
//    @Bean
//    public Step masterStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, PartitionHandler partitionHandler) {
//        return new StepBuilder("masterStep", jobRepository)
//                .partitioner("slaveStep", partitioner())
//                .partitionHandler(partitionHandler)
//                .build();
//    }
//
//    @Bean
//    public Step slaveStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, EntityManagerFactory entityManagerFactory) {
//        return new StepBuilder("slaveStep", jobRepository)
//                .<IctEncoursBrut, IctEncoursBrutDto>chunk(chunkSize, transactionManager)
//                .reader(partitionedItemReader(null, entityManagerFactory)) // Ne pas passer null explicitement
//                .processor(itemProcessor(null, null, null)) // Ajoutez les AtomicInteger ici
//                .writer(staxEventItemWriter(null, new AtomicInteger(), new AtomicInteger())) // Ajoutez les AtomicInteger ici
//                .listener(new CustomStepExecutionListener())
//                .allowStartIfComplete(true)
//                .build();
//    }
//
//    @Bean
//    public Step fileToSftpStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, IntegrationConfig.CustomGateway sftpGateway) {
//        return new StepBuilder("fileToSftpStep", jobRepository)
//                .tasklet((contribution, chunkContext) -> {
//                    int fileIndex = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().getInt("fileIndex", 1);
//
//                    File file = new FileSystemResource("file_part_" + fileIndex + ".xml").getFile();
//                    if (!file.exists()) {
//                        log.warn("File file_part_" + fileIndex + ".xml does not exist");
//                        throw new RuntimeException("File not found: file_part_" + fileIndex + ".xml");
//                    }
//
//                    try {
//                        log.info("Sending file: file_part_" + fileIndex + ".xml");
//                        sftpGateway.sendToSftp(file);
//                    } catch (Exception e) {
//                        log.error("Failed to send file to SFTP", e);
//                        throw new RuntimeException("SFTP file transfer failed", e);
//                    }
//
//                    chunkContext.getStepContext().getStepExecution().getExecutionContext().putInt("fileIndex", fileIndex + 1);
//                    return RepeatStatus.FINISHED;
//                }, transactionManager)
//                .allowStartIfComplete(true)
//                .build();
//    }
//
//    @Bean
//    public PartitionHandler partitionHandler(Step slaveStep, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
//        TaskExecutorPartitionHandler partitionHandler = new TaskExecutorPartitionHandler();
//        partitionHandler.setStep(slaveStep);
//        partitionHandler.setTaskExecutor(new SimpleAsyncTaskExecutor());
//        partitionHandler.setGridSize(4);
//        return partitionHandler;
//    }
//
//    @Bean
//    public Partitioner partitioner() {
//        return (gridSize) -> {
//            Map<String, ExecutionContext> partitionMap = new HashMap<>();
//
//            for (int i = 0; i < gridSize; i++) {
//                ExecutionContext context = new ExecutionContext();
//                context.putInt("partitionIndex", i);
//                partitionMap.put("partition" + i, context);
//            }
//
//            return partitionMap;
//        };
//    }
//
//    @Bean
//    @StepScope
//    public JpaPagingItemReader<IctEncoursBrut> partitionedItemReader(
//            @Value("#{stepExecutionContext['partitionIndex']}") Integer partitionIndex,
//            EntityManagerFactory entityManagerFactory) {
//
//        return new JpaPagingItemReaderBuilder<IctEncoursBrut>()
//                .name("ictEncoursBrutReader" + partitionIndex)
//                .entityManagerFactory(entityManagerFactory)
//                .queryString("SELECT e FROM IctEncoursBrut e")
//                .pageSize(chunkSize)
//                .build();
//    }
//
//    @Bean
//    @StepScope
//    public ItemProcessor<IctEncoursBrut, IctEncoursBrutDto> itemProcessor(
//            IctEncoursBrutMapper mapper,
//            @Value("#{jobExecutionContext['fileIndex']}") AtomicInteger fileIndex,
//            @Value("#{jobExecutionContext['recordsCount']}") AtomicInteger recordsCount) {
//
//        return item -> {
//            recordsCount.incrementAndGet();
//
//            // Si recordsCount atteint la limite, on passe au fichier suivant
//            if (recordsCount.get() >= recordsPerFile) {
//                fileIndex.incrementAndGet();
//                recordsCount.set(0); // Réinitialise le compteur pour le fichier suivant
//            }
//
//            return mapper.entityToDto(item);
//        };
//    }
//
//    @Bean
//    @StepScope
//    public StaxEventItemWriter<IctEncoursBrutDto> staxEventItemWriter(
//            @Value("#{stepExecutionContext['partitionIndex']}") Integer partitionIndex,
//            @Value("#{jobExecutionContext['fileIndex']}") AtomicInteger fileIndex,
//            @Value("#{jobExecutionContext['recordsCount']}") AtomicInteger recordsCount) {
//
//        // Créez un fichier unique basé sur l'index de fichier
//        FileSystemResource resource = new FileSystemResource("file_part_" + fileIndex.get() + ".xml");
//
//        StaxEventItemWriter<IctEncoursBrutDto> writer = new StaxEventItemWriterBuilder<IctEncoursBrutDto>()
//                .name("ictEncoursBrutWriter" + partitionIndex)
//                .resource(resource)
//                .marshaller(jaxbMarshaller())
//                .rootTagName("IctEncoursBruts")
//                .build();
//
//        writer.setShouldDeleteIfEmpty(true); // Supprime le fichier si vide
//
//        return writer;
//    }
//
//    @Bean
//    public Jaxb2Marshaller jaxbMarshaller() {
//        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
//        marshaller.setClassesToBeBound(IctEncoursBrutDto.class);
//        return marshaller;
//    }
//
//    @Bean
//    public JobExecutionListener jobExecutionListener() {
//        return new JobExecutionListener() {
//            @Override
//            public void beforeJob(JobExecution jobExecution) {
//                log.info("Before Job: {}", jobExecution);
//                jobExecution.getExecutionContext().putInt("fileIndex", 1);
//                jobExecution.getExecutionContext().putInt("recordsCount", 0); // Initialiser le compteur d'enregistrements
//            }
//
//            @Override
//            public void afterJob(JobExecution jobExecution) {
//                log.info("After Job: {}", jobExecution);
//            }
//        };
//    }
//}




/*package net.mehdi.springbatch.batch;

import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import net.mehdi.springbatch.batch.listener.CustomStepExecutionListener;
import net.mehdi.springbatch.dto.IctEncoursBrutDto;
import net.mehdi.springbatch.entities.IctEncoursBrut;
import net.mehdi.springbatch.integration.IntegrationConfig;
import net.mehdi.springbatch.mappers.IctEncoursBrutMapper;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Value("${application.batch.chunkSize}")
    private Integer chunkSize;

    @Value("${application.batch.recordsPerFile}")
    private Integer recordsPerFile;

    @Bean
    public Job dailyJob(EntityManagerFactory entityManagerFactory, JobRepository jobRepository, PlatformTransactionManager transactionManager, IntegrationConfig.CustomGateway customGateway) {
        return new JobBuilder("dailyJob", jobRepository)
                .start(databaseToFileStep(entityManagerFactory, jobRepository, transactionManager))
                .next(fileToSftpStep(jobRepository, transactionManager, customGateway))
                .listener(jobExecutionListener())
                .build();
    }

    @Bean
    public Step databaseToFileStep(EntityManagerFactory entityManagerFactory, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("databaseToFileStep", jobRepository)
                .<IctEncoursBrut, IctEncoursBrutDto>chunk(chunkSize, transactionManager)
                .reader(itemReader(entityManagerFactory))
                .processor(itemProcessor(null))
                .writer(multiFileWriter(null, marshaller())) // Assurez-vous que le marshaller est passé ici
                .listener(new CustomStepExecutionListener())
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Step fileToSftpStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, IntegrationConfig.CustomGateway sftpGateway) {
        return new StepBuilder("fileToSftpStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    int fileIndex = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().getInt("fileIndex");

                    File file = new FileSystemResource("file" + fileIndex + ".xml").getFile();
                    if (!file.exists()) {
                        log.warn("File file" + fileIndex + ".xml does not exist");
                        throw new RuntimeException("File not found: file" + fileIndex + ".xml");
                    }

                    try {
                        log.info("Sending file: file" + fileIndex + ".xml");
                        sftpGateway.sendToSftp(file);
                    } catch (Exception e) {
                        log.error("Failed to send file to SFTP", e);
                        throw new RuntimeException("SFTP file transfer failed", e);
                    }

                    chunkContext.getStepContext().getStepExecution().getExecutionContext().putInt("fileIndex", fileIndex + 1);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public ItemProcessor<IctEncoursBrut, IctEncoursBrutDto> itemProcessor(IctEncoursBrutMapper mapper) {
        return mapper::entityToDto;
    }

    @Bean
    public JpaPagingItemReader<IctEncoursBrut> itemReader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<IctEncoursBrut>()
                .name("ictEncoursBrutReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT e FROM IctEncoursBrut e")
                .pageSize(chunkSize)
                .build();
    }

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(IctEncoursBrutDto.class);
        return marshaller;
    }

    @Bean
    public JobExecutionListener jobExecutionListener() {
        return new JobExecutionListener() {
            @Override
            public void beforeJob(JobExecution jobExecution) {
                log.info("Before Job: {}", jobExecution);
                jobExecution.getExecutionContext().putInt("fileIndex", 1);
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
                log.info("After Job: {}", jobExecution);
            }
        };
    }

    @Bean
    @StepScope
    public MultiFileWriter<IctEncoursBrutDto> multiFileWriter(@Value("#{jobExecutionContext['fileIndex']}") Integer fileIndex, Jaxb2Marshaller marshaller) {
        log.info("Creating writer for file index: {}", fileIndex);
        if (fileIndex == null) {
            fileIndex = 1;
        }

        return new MultiFileWriter<>(marshaller, recordsPerFile);
    }

    // Classe MultiFileWriter qui gère l'écriture dans plusieurs fichiers
    public static class MultiFileWriter<T> extends StaxEventItemWriter<T> {
        private final AtomicInteger recordCount = new AtomicInteger(0);
        private final AtomicInteger fileIndex = new AtomicInteger(1);
        private final Jaxb2Marshaller marshaller;
        private final int recordsPerFile;

        public MultiFileWriter(Jaxb2Marshaller marshaller, int recordsPerFile) {
            this.marshaller = marshaller;
            this.recordsPerFile = recordsPerFile;
            setMarshaller(marshaller);
            // Initialiser la ressource pour le premier fichier
            setResource(new FileSystemResource("file" + fileIndex.get() + ".xml"));
        }


        public void write(List<? extends T> items) throws Exception {
            for (T item : items) {
                // Si le nombre d'enregistrements atteint la limite, créer un nouveau fichier
                if (recordCount.getAndIncrement() >= recordsPerFile) {
                    fileIndex.incrementAndGet();
                    recordCount.set(0); // Réinitialiser le compteur

                    // Mettre à jour la ressource pour le nouveau fichier
                    setResource(new FileSystemResource("file" + fileIndex.get() + ".xml"));
                    open(new ExecutionContext()); // Ouvrir un nouveau fichier
                }

                write(List.of(item)); // Utiliser super.write pour écrire dans le fichier actuel
            }
        }

        @Override
        public void open(ExecutionContext executionContext) {
            // S'assurer que l'ouverture du fichier est correctement gérée pour chaque nouveau fichier
            if (executionContext == null) {
                executionContext = new ExecutionContext();
            }
            super.open(executionContext);
        }
    }

}

*/




/*
package net.mehdi.springbatch.batch;

import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import net.mehdi.springbatch.batch.listener.CustomStepExecutionListener;
import net.mehdi.springbatch.dto.IctEncoursBrutDto;
import net.mehdi.springbatch.entities.IctEncoursBrut;
import net.mehdi.springbatch.integration.IntegrationConfig;
import net.mehdi.springbatch.mappers.IctEncoursBrutMapper;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;

@Slf4j
@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Value("${application.batch.chunkSize}")
    private Integer chunkSize;

    @Bean
    public Job dailyJob(EntityManagerFactory entityManagerFactory, JobRepository jobRepository, PlatformTransactionManager transactionManager, IntegrationConfig.CustomGateway customGateway) {
        return new JobBuilder("dailyJob", jobRepository)
                .start(databaseToFileStep(entityManagerFactory, jobRepository, transactionManager))
                .next(fileToSftpStep(jobRepository, transactionManager, customGateway))
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        log.info("Before Job: {}", jobExecution);
                        jobExecution.getExecutionContext().putInt("fileIndex", 1); // Initialiser l'index de fichier
                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        log.info("After Job: {}", jobExecution);
                    }
                })
                .build();
    }

    @Bean
    public Step databaseToFileStep(EntityManagerFactory entityManagerFactory, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("databaseToFileStep", jobRepository)
                .<IctEncoursBrut, IctEncoursBrutDto>chunk(chunkSize, transactionManager)
                .reader(itemReader(entityManagerFactory))
                .processor(itemProcessor(null))
                .writer(itemWriter(null))  // On passe ici un paramètre pour la ressource dynamique
                .listener(new CustomStepExecutionListener())
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Step fileToSftpStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, IntegrationConfig.CustomGateway sftpGateway) {
        return new StepBuilder("fileToSftpStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    int fileIndex = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().getInt("fileIndex");
                    File file;

                    while ((file = new FileSystemResource("file_" + fileIndex + ".xml").getFile()).exists()) {
                        try {
                            log.info("Envoi du fichier: file_{}.xml", fileIndex);
                            sftpGateway.sendToSftp(file);
                        } catch (Exception e) {
                            log.error("Failed to send file to SFTP", e);
                            throw new RuntimeException("SFTP file transfer failed", e);
                        }
                        fileIndex++;
                    }

                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public ItemProcessor<IctEncoursBrut, IctEncoursBrutDto> itemProcessor(IctEncoursBrutMapper mapper) {
        return mapper::entityToDto;
    }

    @Bean
    public JpaPagingItemReader<IctEncoursBrut> itemReader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<IctEncoursBrut>()
                .name("ictEncoursBrutReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT e FROM IctEncoursBrut e")
                .pageSize(chunkSize)
                .build();
    }

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(IctEncoursBrutDto.class);
        return marshaller;
    }

    @Bean
    @StepScope
    public StaxEventItemWriter<IctEncoursBrutDto> itemWriter(@Value("#{jobExecutionContext['fileIndex']}") Integer fileIndex) {
        return new StaxEventItemWriterBuilder<IctEncoursBrutDto>()
                .name("xmlItemWriter")
                .resource(new FileSystemResource("file_" + fileIndex + ".xml"))  // Utilisation du fileIndex dynamique
                .rootTagName("IctEncoursBruts")
                .marshaller(marshaller())
                .build();
    }
}
*/


/*
package net.mehdi.springbatch.batch;

import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import net.mehdi.springbatch.batch.listener.CustomStepExecutionListener;
import net.mehdi.springbatch.dto.IctEncoursBrutDto;
import net.mehdi.springbatch.entities.IctEncoursBrut;
import net.mehdi.springbatch.integration.IntegrationConfig;
import net.mehdi.springbatch.mappers.IctEncoursBrutMapper;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Value("${application.batch.chunkSize}")
    private Integer chunkSize;

    @Bean
    public JobLauncher jobLauncher(JobRepository jobRepository) throws Exception {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    @Bean
    public Job dailyJob(EntityManagerFactory entityManagerFactory, JobRepository jobRepository, PlatformTransactionManager transactionManager, IntegrationConfig.CustomGateway customGateway) {
        return new JobBuilder("dailyJob", jobRepository)
                .start(databaseToSftpStep(entityManagerFactory, jobRepository, transactionManager, customGateway))
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        log.info("Before Job: {}", jobExecution);
                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        log.info("After Job: {}", jobExecution);
                    }
                })
                .build();
    }

    @Bean
    public Step databaseToSftpStep(EntityManagerFactory entityManagerFactory, JobRepository jobRepository, PlatformTransactionManager transactionManager, IntegrationConfig.CustomGateway customGateway) {
        return new StepBuilder("databaseToSftpStep", jobRepository)
                .<IctEncoursBrut, IctEncoursBrutDto>chunk(chunkSize, transactionManager)
                .listener(new CustomStepExecutionListener() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        // Envoyer la balise d'ouverture au début
                        customGateway.sendToSftp(new ByteArrayInputStream("<IctEncoursBruts>".getBytes(StandardCharsets.UTF_8)));
                    }

                    @Override
                    public ExitStatus afterStep(StepExecution stepExecution) {
                        // Envoyer la balise de fermeture à la fin
                        customGateway.sendToSftp(new ByteArrayInputStream("</IctEncoursBruts>".getBytes(StandardCharsets.UTF_8)));
                        return super.afterStep(stepExecution);
                    }
                })
                .reader(itemReader(entityManagerFactory))
                .processor(itemProcessor(null))
                .writer(chunkWriter(customGateway))  // Utiliser le writer customisé
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public ItemProcessor<IctEncoursBrut, IctEncoursBrutDto> itemProcessor(IctEncoursBrutMapper mapper) {
        return mapper::entityToDto;
    }

    @Bean
    public JpaPagingItemReader<IctEncoursBrut> itemReader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<IctEncoursBrut>()
                .name("ictEncoursBrutReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT e FROM IctEncoursBrut e")
                .pageSize(chunkSize)
                .build();
    }

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(IctEncoursBrutDto.class);
        return marshaller;
    }

    @Bean
    public ItemWriter<IctEncoursBrutDto> chunkWriter(IntegrationConfig.CustomGateway customGateway) {
        return items -> {
            // Convertir le chunk en flux XML
            String chunkXml = convertChunkToXml((List<IctEncoursBrutDto>) items);
            // Envoyer ce chunk au serveur SFTP
            customGateway.sendToSftp(new ByteArrayInputStream(chunkXml.getBytes(StandardCharsets.UTF_8)));
        };
    }

    private String convertChunkToXml(List<IctEncoursBrutDto> items) {
        // Utiliser le marshaller pour transformer les DTO en XML
        StringWriter writer = new StringWriter();
        marshaller().marshal(new JAXBElement<>(new QName("IctEncoursBruts"), List.class, items), new StreamResult(writer));
        return writer.toString();
    }
}
*/




/*package net.mehdi.springbatch.batch;

import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import net.mehdi.springbatch.batch.listener.CustomStepExecutionListener;
import net.mehdi.springbatch.dto.IctEncoursBrutDto;
import net.mehdi.springbatch.entities.IctEncoursBrut;
import net.mehdi.springbatch.integration.IntegrationConfig;
import net.mehdi.springbatch.mappers.IctEncoursBrutMapper;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.InputStream;

@Slf4j
@Configuration
@EnableBatchProcessing
public class BatchConfig {

    public static final String DEFAULT_FILE_PATH = "file.xml";

    @Value("${application.batch.chunkSize}")
    private Integer chunkSize ;

    @Bean
    public JobLauncher jobLauncher(JobRepository jobRepository) throws Exception {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    @Bean
    public Job dailyJob(EntityManagerFactory entityManagerFactory, JobRepository jobRepository, PlatformTransactionManager transactionManager, IntegrationConfig.CustomGateway customGateway) {
        return new JobBuilder("dailyJob", jobRepository)
                .start(databaseToFileStep(entityManagerFactory, jobRepository, transactionManager))
                .next(fileToSftpStep(jobRepository, transactionManager, customGateway))
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        log.info("Before Job: {}", jobExecution);
                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        log.info("After Job: {}", jobExecution);
                    }
                })
                .build();
    }

    @Bean
    public Step databaseToFileStep(EntityManagerFactory entityManagerFactory, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("databaseToFileStep", jobRepository)
                .<IctEncoursBrut, IctEncoursBrutDto>chunk(chunkSize, transactionManager)
                .listener(new CustomStepExecutionListener())
                .reader(itemReader(entityManagerFactory))
                .processor(itemProcessor(null))
                .writer(itemWriter())
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Step fileToSftpStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, IntegrationConfig.CustomGateway sftpGateway) {
        return new StepBuilder("fileToSftpStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    try (InputStream inputStream = new FileSystemResource(DEFAULT_FILE_PATH).getInputStream()) {
                        // Envoyer le fichier en flux
                        sftpGateway.sendToSftp(inputStream);
                    } catch (Exception e) {
                        log.error("Failed to send file to SFTP", e);
                        throw new RuntimeException("SFTP file transfer failed", e);
                    }
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public ItemProcessor<IctEncoursBrut, IctEncoursBrutDto> itemProcessor(IctEncoursBrutMapper mapper) {
        return mapper::entityToDto;
    }

    @Bean
    public JpaPagingItemReader<IctEncoursBrut> itemReader(EntityManagerFactory entityManagerFactory) {
        return new JpaPagingItemReaderBuilder<IctEncoursBrut>()
                .name("ictEncoursBrutReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT e FROM IctEncoursBrut e")
                .pageSize(chunkSize)
                .build();
    }

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(IctEncoursBrutDto.class);
        return marshaller;
    }

    @Bean
    public StaxEventItemWriter<IctEncoursBrutDto> itemWriter() {
        return new StaxEventItemWriterBuilder<IctEncoursBrutDto>()
                .name("xmlItemWriter")
                .resource(new FileSystemResource(DEFAULT_FILE_PATH))
                .rootTagName("IctEncoursBruts")
                .marshaller(marshaller())
                .build();
    }
}
*/































