package store.vxdesign.htat.core.connections;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import store.vxdesign.htat.core.exceptions.ConnectionTimeoutException;
import store.vxdesign.htat.core.exceptions.InstanceInitializationException;
import store.vxdesign.htat.core.exceptions.PatternNotFoundException;
import store.vxdesign.htat.core.utilities.commands.ExpectAndSend;
import store.vxdesign.htat.core.utilities.commands.ShellCommand;
import store.vxdesign.htat.core.utilities.writers.CommandResultWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public abstract class AbstractConnection<P extends ConnectionProperties> implements Connection, Shell {
    protected final Logger logger = LogManager.getLogger(this.getClass());
    protected final int connectionTimeoutInMilliseconds = 10000;
    private final CommandResultWriter commandResultWriter;

    @Getter
    protected final P properties;

    private int timeoutInSeconds = 10;
    private ExecutorService streamReader;
    private ScheduledExecutorService scheduledExecutorService;
    private ScheduledFuture<?> scheduledFuture = null;

    public AbstractConnection(P properties) {
        if (properties != null) {
            this.properties = properties;
            commandResultWriter = CommandResultWriter.create(this);
        } else {
            throw new InstanceInitializationException("Connection instance cannot be created if properties are not stated");
        }
    }

    protected void connect(Runnable connect) {
        if (!isConnected()) {
            logger.trace("Connecting to host: {}@{}:{}", properties.getUser(), properties.getHost(), properties.getPort());
            connect.run();
            logger.debug("Connected to host: {}@{}:{}", properties.getUser(), properties.getHost(), properties.getPort());
        } else {
            logger.trace("Connection to host {}@{}:{} has already exist", properties.getUser(), properties.getHost(), properties.getPort());
            scheduledFuture.cancel(true);
        }
    }

    protected void disconnect(Runnable disconnect) {
        if (isConnected()) {
            logger.trace("Disconnecting from host: {}@{}:{}", properties.getUser(), properties.getHost(), properties.getPort());
            disconnect.run();
            logger.debug("Disconnected from host: {}@{}:{}", properties.getUser(), properties.getHost(), properties.getPort());
        } else {
            logger.trace("Connection to host {}@{}:{} has already closed", properties.getUser(), properties.getHost(), properties.getPort());
        }
    }

    protected abstract boolean isConnected();

    protected CommandResult execute(Supplier<CommandResult> supplier) {
        connect();
        CommandResult result = supplier.get();
        commandResultWriter.write(result);
        scheduledFuture = startClosingTask();
        return result;
    }

    @Override
    public void setTimeoutInSeconds(int timeoutInSeconds) {
        if (timeoutInSeconds >= 0) {
            this.timeoutInSeconds = timeoutInSeconds;
        } else {
            logger.error("Timeout was got incorrect: {}. The value should be more or equal than 0", timeoutInSeconds);
        }
    }

    @Override
    public void close() {
        disconnect();
        if (scheduledFuture != null && !scheduledFuture.isDone()) {
            scheduledFuture.cancel(true);
        }

        if (scheduledExecutorService != null && !scheduledExecutorService.isShutdown()) {
            scheduledExecutorService.shutdown();
        }

        if (streamReader != null && !streamReader.isShutdown()) {
            streamReader.shutdown();
        }

        logger.trace("Connection was closed and service was shutdown");
    }

    private ScheduledFuture startClosingTask() {
        if (scheduledExecutorService == null || scheduledExecutorService.isShutdown()) {
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            logger.trace("Scheduled executor service is created");
        } else {
            logger.trace("Scheduled task will be recreated");
        }
        return scheduledExecutorService.schedule(this::close, timeoutInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public CommandResult execute(String pattern, ShellCommand command) {
        return executor(null, Pattern.compile(pattern), command);
    }

    @Override
    public CommandResult execute(Pattern pattern, ShellCommand command) {
        return executor(null, pattern, command);
    }

    @Override
    public CommandResult execute(int timeout, String pattern, ShellCommand command) {
        return executor(timeout, Pattern.compile(pattern), command);
    }

    @Override
    public CommandResult execute(int timeout, Pattern pattern, ShellCommand command) {
        return executor(timeout, pattern, command);
    }

    protected abstract CommandResult executor(Integer timeout, Pattern pattern, ShellCommand command);

    protected void write(OutputStream outputStream, String command) throws IOException {
        outputStream.write(command.concat("\n").getBytes());
        outputStream.flush();
    }

    protected String read(InputStream inputStream, Integer timeout, Pattern pattern) throws ExecutionException, InterruptedException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.defaultCharset());
        StringBuilder result = new StringBuilder();

        if (streamReader == null || streamReader.isShutdown()) {
            streamReader = Executors.newSingleThreadExecutor();
        }
        Future<String> streamReaderTask = streamReader.submit(() -> {
            int ch;
            do {
                if ((ch = inputStreamReader.read()) == -1) {
                    break;
                } else {
                    result.append((char) ch);
                    if (pattern != null && pattern.matcher(result).find()) {
                        return result.toString();
                    }
                }
            } while (true);

            if (pattern == null || pattern.matcher(result).find()) {
                return result.toString();
            } else {
                throw new PatternNotFoundException("Pattern '%s' was not found: %s", pattern, result);
            }
        });

        long time = System.currentTimeMillis();
        while (true) {
            if (streamReaderTask.isDone()) {
                return streamReaderTask.get();
            }

            if (timeout != null && timeout != 0 && System.currentTimeMillis() - time >= timeout * 1000) {
                streamReaderTask.cancel(true);
                streamReader.shutdownNow();
                throw new ConnectionTimeoutException("Failed to read the input stream because time is over (%d s)", timeout);
            }
        }
    }

    protected void sudoerAuth(OutputStream outputStream, ShellCommand command) throws IOException {
        if (command.isSudoer()) {
            write(outputStream, properties.getPassword());
        }
    }

    protected String expectAndSend(InputStream inputStream, OutputStream outputStream, Integer timeout, ShellCommand command) throws ExecutionException, InterruptedException, IOException {
        String last = null;
        for (ExpectAndSend expectAndSend : command.getExpectAndSends()) {
            read(inputStream, timeout, expectAndSend.getExpected());
            write(outputStream, expectAndSend.getCommand());
            last = expectAndSend.getCommand();
        }
        return last;
    }

    protected void skipInputStreamToEnd(InputStream inputStream) throws IOException {
        logger.trace("Skipping the current marker position to end of input stream");
        while (true) {
            if (!(inputStream.available() > 0 && inputStream.read() != -1)) {
                break;
            }
        }
    }
}
