package net.mehdi.springbatch.integration;

import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.sftp.client.SftpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.sftp.outbound.SftpMessageHandler;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.messaging.Message;

import java.io.File;

@Configuration
@Slf4j
public class IntegrationConfig {

    @Bean
    public CachingSessionFactory<SftpClient.DirEntry> sftpSessionFactory(SftpConfiguration configuration) {
        log.info(configuration.toString());
        DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(true);
        factory.setHost(configuration.getHost());
        factory.setPort(configuration.getPort());
        factory.setUser(configuration.getUser());
        factory.setAllowUnknownKeys(true);
        factory.setTimeout(60000);
        if (configuration.getPassword() != null) {
            factory.setPassword(configuration.getPassword());
        }

        return new CachingSessionFactory<>(factory, 10);
    }

    @Bean
    @ServiceActivator(inputChannel = "toSftpChannel")
    public SftpMessageHandler handler(SftpConfiguration configuration) {
        SftpMessageHandler handler = new SftpMessageHandler(sftpSessionFactory(configuration));
        handler.setRemoteDirectoryExpression(new LiteralExpression(configuration.getDirectory()));
        handler.setLoggingEnabled(true);
        handler.setFileNameGenerator((Message<?> message) -> {
            if (message.getPayload() instanceof File file) {
                return file.getName();
            } else {
                throw new IllegalArgumentException("Expected a File in the message payload");
            }
        });
        return handler;
    }

    @MessagingGateway
    public interface CustomGateway {
        @Gateway(requestChannel = "toSftpChannel")
        void sendToSftp(File file);
    }
}
