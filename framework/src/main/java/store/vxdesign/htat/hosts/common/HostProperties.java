package store.vxdesign.htat.hosts.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import store.vxdesign.htat.core.connections.ssh.SshConnectionProperties;
import store.vxdesign.htat.core.connections.telnet.TelnetConnectionProperties;
import store.vxdesign.htat.core.properties.Properties;

@Getter
@Setter
public abstract class HostProperties implements Properties {
    private String name;
    private OperatingSystemFamily os;

    @Getter(AccessLevel.PROTECTED)
    private SshConnectionProperties ssh;

    @Getter(AccessLevel.PROTECTED)
    private TelnetConnectionProperties telnet;
}
