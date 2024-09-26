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

    @Bean
    public Step modifyFileStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("modifyFileStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    String filePath = DEFAULT_FILE_PATH;
                    try {
                        removeRootTags(filePath);
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
                    String command = "/home/mehdi/script.sh";
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

    public void removeRootTags(String filePath) throws IOException {

        String content = new String(Files.readAllBytes(Paths.get(filePath)));

        content = content.replaceAll("<root>", "").replaceAll("</root>", "");

        Files.write(Paths.get(filePath), content.getBytes());
    }
}



