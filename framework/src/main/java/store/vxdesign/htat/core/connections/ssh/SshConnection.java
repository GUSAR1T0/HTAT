package store.vxdesign.htat.core.connections.ssh;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.io.IOUtils;
import store.vxdesign.htat.core.connections.AbstractConnection;
import store.vxdesign.htat.core.connections.CommandResult;
import store.vxdesign.htat.core.exceptions.ConnectionException;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class SshConnection extends AbstractConnection<SshConnectionProperties> {
    private final int connectionTimeoutInMilliseconds = 6000;
    private final int delayWaitTimeoutInMilliseconds = 10;

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
                if (properties.getOptions() != null) {
                    properties.getOptions().forEach(session::setConfig);
                }
                session.connect(connectionTimeoutInMilliseconds);
            } catch (JSchException e) {
                throw new ConnectionException("Failed to create session for %s@%s:%d: %s", properties.getUser(), properties.getHost(), properties.getPort(), e);
            }
        });
    }

    @Override
    public void disconnect() {
        disconnect(() -> session.disconnect());
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
                    TimeUnit.MILLISECONDS.sleep(delayWaitTimeoutInMilliseconds);
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
                    TimeUnit.MILLISECONDS.sleep(delayWaitTimeoutInMilliseconds);
                }

                return commandResult;
            } catch (InterruptedException | IOException | JSchException e) {
                throw new ConnectionException("Failed to execute command '%s' on %s@%s:%d: %s", commandWithParameters,
                        properties.getUser(), properties.getHost(), properties.getPort(), e);
            }
        });
    }
}
