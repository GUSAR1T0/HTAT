package store.vxdesign.htat.core.connections;

import lombok.Getter;
import lombok.Setter;
import store.vxdesign.htat.core.properties.Properties;

@Getter
@Setter
public abstract class ConnectionProperties implements Properties {
    private String host;
    private int port;
    private String user;
    private String password;
}
