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

}
