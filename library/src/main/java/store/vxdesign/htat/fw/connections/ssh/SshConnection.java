package store.vxdesign.htat.fw.connections.ssh;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import store.vxdesign.htat.fw.connections.AbstractConnection;
import store.vxdesign.htat.fw.connections.CommandResult;
import store.vxdesign.htat.fw.exceptions.ConnectionException;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Service
public class SshConnection extends AbstractConnection<SshConnectionProperties> {
    private final int connectionTimeout = 6000;
    private final int delayWaitTimeout = 100;

    private JSch jsch;
    private Session session;
    private ChannelExec channel;

    public SshConnection(SshConnectionProperties properties) {
        super(properties);
        jsch = new JSch();
    }

    @Override
    public void connect() {
        connect(() -> {
            try {
                session = jsch.getSession(properties.getUser(), properties.getHost(), properties.getPort());
                session.setPassword(properties.getPassword());
                session.setDaemonThread(true);
                properties.getOptions().forEach(session::setConfig);
                session.connect(connectionTimeout);
            } catch (JSchException e) {
                throw new ConnectionException("Failed to create session for %s@%s:%d: %s", properties.getUser(), properties.getHost(), properties.getPort(), e);
            }
        });
    }

    @Override
    public void disconnect() {
        disconnect(() -> {
            if (session != null) {
                session.disconnect();
            }
        });
    }

    @Override
    protected boolean isConnected() {
        return session != null && session.isConnected();
    }

    @Override
    public CommandResult execute(String command, String... parameters) {
        return execute(() -> {
            String commandWithParameters = String.format("%s %s", command, String.join(" ", parameters)).trim();
            try {
                channel = (ChannelExec) session.openChannel("exec");
                channel.run();
            } catch (JSchException e) {
                throw new ConnectionException("Failed to create channel for %s@%s:%d: %s", properties.getUser(), properties.getHost(), properties.getPort(), e);
            }

            try (PipedOutputStream errPipe = new PipedOutputStream();
                 PipedInputStream errIs = new PipedInputStream(errPipe);
                 InputStream is = channel.getInputStream()) {
                channel.setInputStream(null);
                channel.setErrStream(errPipe);
                channel.setCommand(commandWithParameters);

                channel.connect();
                while (!channel.isConnected()) {
                    TimeUnit.SECONDS.sleep(delayWaitTimeout);
                }

                LocalDateTime start = LocalDateTime.now();

                channel.start();

                // The lines below can be wrong
                String output = IOUtils.toString(is, Charset.defaultCharset());
                String error = IOUtils.toString(errIs, Charset.defaultCharset());

                LocalDateTime end = LocalDateTime.now();

                CommandResult commandResult = CommandResult.builder().
                        setStart(start).
                        setCommand(commandWithParameters).
                        setPattern(Pattern.compile("")).
                        setStatus(channel.getExitStatus()).
                        setOutput(output).
                        setError(error).
                        setEnd(end).
                        build();

                channel.disconnect();
                while (channel.isConnected()) {
                    TimeUnit.SECONDS.sleep(delayWaitTimeout);
                }

                return commandResult;
            } catch (InterruptedException | IOException | JSchException e) {
                throw new ConnectionException("Failed to execute command '%s' on %s@%s:%d: %s", commandWithParameters,
                        properties.getUser(), properties.getHost(), properties.getPort(), e);
            }
        });
    }
}
