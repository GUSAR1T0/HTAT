package store.vxdesign.htat.core.connections;

public interface Shell {
    CommandResult execute(String command, String... parameters);

    void setTimeoutInSeconds(int timeoutInSeconds);

    void close();
}
