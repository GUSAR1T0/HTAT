package store.vxdesign.htat.fw.connections.telnet;

import org.apache.commons.net.telnet.TelnetClient;
import store.vxdesign.htat.fw.connections.AbstractConnection;
import store.vxdesign.htat.fw.connections.CommandResult;
import store.vxdesign.htat.fw.connections.ConnectionPatterns;
import store.vxdesign.htat.fw.exceptions.ConnectionException;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class TelnetConnection extends AbstractConnection<TelnetConnectionProperties> {
    private final int connectionTimeoutInMilliseconds = 10;

    private TelnetClient client;

    public TelnetConnection(TelnetConnectionProperties properties) {
        super(properties);
        client = new TelnetClient();
    }

    @Override
    public void connect() {
        connect(() -> {
            try {
                client.connect(properties.getHost(), properties.getPort());

                while (!client.isConnected()) {
                    TimeUnit.SECONDS.sleep(connectionTimeoutInMilliseconds);
                }

                read(ConnectionPatterns.login);
                write(properties.getUser());

                read(ConnectionPatterns.password);
                write(properties.getPassword());

                read(ConnectionPatterns.prompt);
            } catch (InterruptedException | IOException e) {
                throw new ConnectionException("Failed to connect to %s@%s:%d: %s", properties.getUser(), properties.getHost(), properties.getPort(), e);
            }
        });
    }

    @Override
    public void disconnect() {
        disconnect(() -> {
            try {
                client.disconnect();
            } catch (IOException e) {
                throw new ConnectionException("Failed to disconnect from %s@%s:%d: %s", properties.getUser(), properties.getHost(), properties.getPort(), e);
            }
        });
    }

    @Override
    protected boolean isConnected() {
        return client.isConnected();
    }

    @Override
    public CommandResult execute(String command, String... parameters) {
        return execute(() -> {
            String commandWithParameters = String.format("%s %s", command, String.join(" ", parameters)).trim();
            try {
                LocalDateTime start = LocalDateTime.now();

                write(commandWithParameters);

                String output = read(ConnectionPatterns.prompt).
                        replaceFirst(commandWithParameters, "").
                        replaceAll("\\S*@\\S*$", "");

                LocalDateTime end = LocalDateTime.now();

                // TODO: Add defining of exit status
                return CommandResult.builder().
                        setStart(start).
                        setCommand(commandWithParameters).
                        setPattern(Pattern.compile("")).
                        setStatus(0).
                        setOutput(output).
                        setError("").
                        setEnd(end).
                        build();
            } catch (Exception e) {
                throw new ConnectionException("Failed to execute command '%s' on %s@%s:%d: %s", commandWithParameters,
                        properties.getUser(), properties.getHost(), properties.getPort(), e);
            }
        });
    }

    private String read(Pattern pattern) throws IOException {
        InputStream in = client.getInputStream();
        StringBuilder result = new StringBuilder();
        int ch;
        while ((ch = in.read()) != -1 &&
                (pattern == null || !pattern.matcher(String.format("%s%c", result, ch)).find())) {
            result.append((char) ch);
        }
        return result.toString();
    }

    private void write(String command) throws IOException {
        client.getOutputStream().write(command.concat("\n").getBytes());
        client.getOutputStream().flush();
    }
}
