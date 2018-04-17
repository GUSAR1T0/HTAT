package store.vxdesign.htat.core.connections.telnet;

import org.apache.commons.net.telnet.TelnetClient;
import store.vxdesign.htat.core.connections.AbstractConnection;
import store.vxdesign.htat.core.connections.CommandResult;
import store.vxdesign.htat.core.connections.ConnectionPatterns;
import store.vxdesign.htat.core.exceptions.ConnectionHandlingException;
import store.vxdesign.htat.core.utilities.commands.ShellCommand;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class TelnetConnection extends AbstractConnection<TelnetConnectionProperties> {
    private final int connectionWaitingTimeoutInMilliseconds = 10;

    private TelnetClient client;

    public TelnetConnection(TelnetConnectionProperties properties) {
        super(properties);
        client = new TelnetClient();
    }

    @Override
    public void connect() {
        connect(() -> {
            try {
                client.setConnectTimeout(connectionTimeoutInMilliseconds);
                client.connect(properties.getHost(), properties.getPort());
                while (!client.isConnected()) {
                    TimeUnit.SECONDS.sleep(connectionWaitingTimeoutInMilliseconds);
                }

                InputStream inputStream = client.getInputStream();
                OutputStream outputStream = client.getOutputStream();

                logger.trace("Authenticating as {} on host {}:{}", properties.getUser(), properties.getHost(), properties.getPort());
                read(inputStream, 10, ConnectionPatterns.login);
                write(outputStream, properties.getUser());

                read(inputStream, 10, ConnectionPatterns.password);
                write(outputStream, properties.getPassword());

                read(inputStream, 10, ConnectionPatterns.prompt);
                logger.trace("Authentication was passed successfully");
            } catch (InterruptedException | ExecutionException | IOException e) {
                throw new ConnectionHandlingException("Failed to connect to %s@%s:%d: %s", properties.getUser(), properties.getHost(), properties.getPort(), e);
            }
        });
    }

    @Override
    public void disconnect() {
        disconnect(() -> {
            try {
                client.disconnect();
            } catch (IOException e) {
                throw new ConnectionHandlingException("Failed to disconnect from %s@%s:%d: %s", properties.getUser(), properties.getHost(), properties.getPort(), e);
            }
        });
    }

    @Override
    protected boolean isConnected() {
        return client.isConnected();
    }

    @Override
    public CommandResult execute(ShellCommand command) {
        return executor(null, ConnectionPatterns.prompt, command);
    }

    @Override
    public CommandResult execute(int timeout, ShellCommand command) {
        return executor(timeout, ConnectionPatterns.prompt, command);
    }

    @Override
    protected CommandResult executor(Integer timeout, Pattern pattern, ShellCommand command) {
        return execute(() -> {
            try {
                InputStream inputStream = client.getInputStream();
                OutputStream outputStream = client.getOutputStream();

                LocalDateTime start = LocalDateTime.now();

                logger.debug("Executing the command: {}", command);
                write(outputStream, command.toString());

                sudoerAuth(outputStream, command);

                String lastSentCommand = expectAndSend(inputStream, outputStream, timeout, command);

                logger.debug("Getting a result of execution");
                String output = read(inputStream, timeout, pattern).
                        replaceFirst(command.toString(), "").
                        replaceFirst(command.isSudoer() ? properties.getPassword() : "", "").
                        replaceFirst(lastSentCommand != null ? lastSentCommand : "", "").
                        replaceAll(pattern != null ? pattern.pattern() : ConnectionPatterns.prompt.pattern(), "").
                        trim();

                LocalDateTime end = LocalDateTime.now();

                skipInputStreamToEnd(inputStream);

                CommandResult result = CommandResult.builder().
                        setStart(start).
                        setCommand(command.toString()).
                        setTimeout(timeout).
                        setPattern(pattern).
                        setStatus(0).
                        setOutput(output).
                        setError("").
                        setEnd(end).
                        build();
                logger.trace("The result of execution is:\n{}", result);
                return result;
            } catch (InterruptedException | ExecutionException | IOException e) {
                throw new ConnectionHandlingException("Failed to execute command '%s' on %s@%s:%d: %s", command,
                        properties.getUser(), properties.getHost(), properties.getPort(), e);
            }
        });
    }
}
