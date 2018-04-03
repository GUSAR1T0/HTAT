package store.vxdesign.htat.fw.connections;

public interface Shell {
    CommandResult execute(String command, String... parameters);

    void setTimeoutInSeconds(int timeoutInSeconds);

    void close();
}
