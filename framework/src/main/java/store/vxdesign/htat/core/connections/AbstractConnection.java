package store.vxdesign.htat.core.connections;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import store.vxdesign.htat.core.exceptions.ConnectionTimeoutException;
import store.vxdesign.htat.core.exceptions.InstanceInitializationException;
import store.vxdesign.htat.core.exceptions.PatternNotFoundException;
import store.vxdesign.htat.core.utilities.commands.ShellCommand;

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
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Getter
    protected final P properties;

    private int timeoutInSeconds = 10;
    private ExecutorService streamReader;
    private ScheduledExecutorService scheduledExecutorService;
    private ScheduledFuture<?> scheduledFuture = null;

    public AbstractConnection(P properties) {
        if (properties != null) {
            this.properties = properties;
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

    protected String read(Integer timeout, Pattern pattern, InputStream inputStream) throws ExecutionException, InterruptedException {
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

    protected void skipInputStreamToEnd(InputStream inputStream) throws IOException {
        logger.trace("Skipping the current marker position to end of input stream");
        while (true) {
            if (!(inputStream.available() > 0 && inputStream.read() != -1)) {
                break;
            }
        }
    }
}
