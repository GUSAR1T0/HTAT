package store.vxdesign.htat.hosts.common;

import java.util.function.Function;

public abstract class AbstractHost<P extends HostProperties, C extends HostConnections> implements Host<P, C> {
    protected final P properties;
    protected final C connections;

    public AbstractHost(P properties, Function<P, C> connectionsGenerator) {
        this.properties = properties;
        this.connections = connectionsGenerator.apply(properties);
    }
}
