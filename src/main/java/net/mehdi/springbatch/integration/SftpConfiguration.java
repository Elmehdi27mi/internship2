package net.mehdi.springbatch.integration;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Configuration
@ConfigurationProperties(prefix="application.sftp")
public class SftpConfiguration {
    private String host;
    private Integer port;
    private String user;
    private String password;
    private String directory;
}
