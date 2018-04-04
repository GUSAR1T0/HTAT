package store.vxdesign.htat.core.connections;

import lombok.Getter;
import store.vxdesign.htat.core.exceptions.ConnectionException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public abstract class AbstractConnection<P extends ConnectionProperties> implements Connection, Shell {
    @Getter
    protected final P properties;

    private int timeoutInSeconds = 10;
    private ScheduledExecutorService scheduledExecutorService;
    private ScheduledFuture<?> scheduledFuture = null;

    public AbstractConnection(P properties) {
        if (properties != null) {
            this.properties = properties;
        } else {
            throw new ConnectionException("Connection cannot to create if properties are not stated");
        }
    }

    protected void connect(Runnable connect) {
        if (!isConnected()) {
            connect.run();
            System.out.println("Connected to host");
        } else {
            System.out.println("Connection to host has already exist");
            scheduledFuture.cancel(true);
        }
    }

    protected void disconnect(Runnable disconnect) {
        if (isConnected()) {
            disconnect.run();
            System.out.println("Disconnected from host");
        } else {
            System.out.println("Connection to host has already closed");
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
            // TODO: Add error log message
        }
    }

    @Override
    public void close() {
        disconnect();
        if (scheduledFuture != null && !scheduledFuture.isDone()) {
            scheduledFuture.cancel(true);
        }
        scheduledExecutorService.shutdown();
        System.out.println("Disconnected and service is closed");
    }

    private ScheduledFuture startClosingTask() {
        if (scheduledExecutorService == null || scheduledExecutorService.isShutdown()) {
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            System.out.println("Scheduled executor service is created");
        } else {
            System.out.println("Scheduled task will be recreated");
        }
        return scheduledExecutorService.schedule(this::close, timeoutInSeconds, TimeUnit.SECONDS);
    }
}
