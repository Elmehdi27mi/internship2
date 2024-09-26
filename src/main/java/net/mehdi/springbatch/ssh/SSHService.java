package net.mehdi.springbatch.ssh;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Properties;

@Slf4j
@Service
@AllArgsConstructor
@NoArgsConstructor
public class SSHService {

    @Value("${application.sftp.host}")
    private String host;

    @Value("${application.sftp.port}")
    private int port;

    @Value("${application.sftp.user}")
    private String user;

    @Value("${application.sftp.password}")
    private String password;

    public String executeCommand(String command) {
        String result = "";
        Session session = null;
        ChannelExec channel = null;
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(user, host, port);
            session.setPassword(password);

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            session.connect();

            channel = (ChannelExec) session.openChannel("exec");
            // Exécuter le fichier .sh ou la commande
            channel.setCommand(command);
            InputStream in = channel.getInputStream();
            channel.connect();

            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    result += new String(tmp, 0, i);
                }
                if (channel.isClosed()) {
                    if (in.available() > 0) continue;
                    log.info("Exit status: {}", channel.getExitStatus());
                    break;
                }
            }
        } catch (Exception e) {
            log.error("Error executing SSH command", e);
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
        return result;
    }
}
