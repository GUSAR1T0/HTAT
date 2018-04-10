package store.vxdesign.htat.core.connections.ssh;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import store.vxdesign.htat.core.connections.AbstractConnection;
import store.vxdesign.htat.core.connections.CommandResult;
import store.vxdesign.htat.core.exceptions.ConnectionException;
import store.vxdesign.htat.core.utilities.commands.ShellCommand;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
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
    public CommandResult execute(ShellCommand command) {
        return executor(null, null, command);
    }

    @Override
    public CommandResult execute(int timeout, ShellCommand command) {
        return executor(timeout, null, command);
    }

    @Override
    protected CommandResult executor(Integer timeout, Pattern pattern, ShellCommand command) {
        return execute(() -> {
            try {
                channel = (ChannelExec) session.openChannel("exec");
                channel.run();
            } catch (JSchException e) {
                throw new ConnectionException("Failed to create channel for %s@%s:%d: %s", properties.getUser(), properties.getHost(), properties.getPort(), e);
            }

            try (OutputStream outputStream = channel.getOutputStream();
                 InputStream inputStream = channel.getInputStream()) {
                channel.setInputStream(null);
                channel.setPty(true);
                channel.setCommand(command.toString());

                channel.connect();
                while (!channel.isConnected()) {
                    TimeUnit.MILLISECONDS.sleep(delayWaitTimeoutInMilliseconds);
                }

                LocalDateTime start = LocalDateTime.now();

                if (command.isSudoer()) {
                    write(outputStream, properties.getPassword());
                }

                String message = read(timeout, pattern, inputStream).
                        replaceFirst(command.isSudoer() ? properties.getPassword() : "", "").
                        trim();

                LocalDateTime end = LocalDateTime.now();

                skipInputStreamToEnd(inputStream);

                int status;
                while (true) {
                    if (channel.isClosed()) {
                        status = channel.getExitStatus();
                        break;
                    }
                    TimeUnit.MILLISECONDS.sleep(delayWaitTimeoutInMilliseconds);
                }

                channel.disconnect();
                while (channel.isConnected()) {
                    TimeUnit.MILLISECONDS.sleep(delayWaitTimeoutInMilliseconds);
                }

                return CommandResult.builder().
                        setStart(start).
                        setCommand(command.toString()).
                        setTimeout(timeout).
                        setPattern(pattern).
                        setStatus(status).
                        setOutput(status == 0 ? message : "").
                        setError(status != 0 ? message : "").
                        setEnd(end).
                        build();
            } catch (InterruptedException | ExecutionException | IOException | JSchException e) {
                throw new ConnectionException("Failed to execute command '%s' on %s@%s:%d: %s", command,
                        properties.getUser(), properties.getHost(), properties.getPort(), e);
            }
        });
    }
}
