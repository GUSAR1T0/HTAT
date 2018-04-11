package store.vxdesign.htat.core.connections.ssh;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import store.vxdesign.htat.core.connections.AbstractConnection;
import store.vxdesign.htat.core.connections.CommandResult;
import store.vxdesign.htat.core.exceptions.ConnectionHandlingException;
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
                logger.trace("Creating the session to host {}@{}:{}", properties.getUser(), properties.getHost(), properties.getPort());
                session = jsch.getSession(properties.getUser(), properties.getHost(), properties.getPort());
                session.setPassword(properties.getPassword());

                logger.trace("Making session as daemon");
                session.setDaemonThread(true);

                logger.trace("Setting the options: {}", properties.getOptions());
                if (properties.getOptions() != null) {
                    properties.getOptions().forEach(session::setConfig);
                }

                session.connect(connectionTimeoutInMilliseconds);
            } catch (JSchException e) {
                throw new ConnectionHandlingException("Failed to create session for %s@%s:%d: %s", properties.getUser(), properties.getHost(), properties.getPort(), e);
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
                logger.trace("Opening the 'exec' channel");
                channel = (ChannelExec) session.openChannel("exec");
                channel.run();
            } catch (JSchException e) {
                throw new ConnectionHandlingException("Failed to create channel for %s@%s:%d: %s", properties.getUser(), properties.getHost(), properties.getPort(), e);
            }

            try (OutputStream outputStream = channel.getOutputStream();
                 InputStream inputStream = channel.getInputStream()) {
                channel.setInputStream(null);

                logger.trace("Setting pseudo terminal for connection");
                channel.setPty(true);

                logger.debug("Executing the command: {}", command);
                channel.setCommand(command.toString());
                channel.connect();
                while (!channel.isConnected()) {
                    TimeUnit.MILLISECONDS.sleep(delayWaitTimeoutInMilliseconds);
                }

                LocalDateTime start = LocalDateTime.now();

                if (command.isSudoer()) {
                    write(outputStream, properties.getPassword());
                }

                logger.debug("Getting a result of execution");
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

                logger.trace("Closing the 'exec' channel");
                channel.disconnect();
                while (channel.isConnected()) {
                    TimeUnit.MILLISECONDS.sleep(delayWaitTimeoutInMilliseconds);
                }

                CommandResult result = CommandResult.builder().
                        setStart(start).
                        setCommand(command.toString()).
                        setTimeout(timeout).
                        setPattern(pattern).
                        setStatus(status).
                        setOutput(status == 0 ? message : "").
                        setError(status != 0 ? message : "").
                        setEnd(end).
                        build();
                logger.trace("The result of execution is:\n{}", result);
                return result;
            } catch (InterruptedException | ExecutionException | IOException | JSchException e) {
                throw new ConnectionHandlingException("Failed to execute command '%s' on %s@%s:%d: %s", command,
                        properties.getUser(), properties.getHost(), properties.getPort(), e);
            }
        });
    }
}
