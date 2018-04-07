package store.vxdesign.htat.hosts.common;

public interface Host<P extends HostProperties, C extends HostConnections> {
    P getProperties();

    C getConnections();
}
