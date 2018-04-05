package store.vxdesign.htat.hosts.common;

import store.vxdesign.htat.core.connections.Connections;
import store.vxdesign.htat.core.connections.Shell;
import store.vxdesign.htat.core.connections.ssh.SshConnection;
import store.vxdesign.htat.core.connections.telnet.TelnetConnection;

public abstract class HostConnections extends Connections {
    private HostProperties properties;

    public HostConnections(HostProperties properties) {
        this.properties = properties;
    }

    public Shell getSshConnection() {
        return getConnection(SshConnection.class, properties.getSsh());
    }

    public Shell getTelnetConnection() {
        return getConnection(TelnetConnection.class, properties.getTelnet());
    }
}
