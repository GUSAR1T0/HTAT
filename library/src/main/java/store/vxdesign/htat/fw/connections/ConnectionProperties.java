package store.vxdesign.htat.fw.connections;

import lombok.Getter;
import lombok.Setter;
import store.vxdesign.htat.fw.properties.Properties;

public abstract class ConnectionProperties implements Properties {
    @Getter
    @Setter
    private String host;

    @Getter
    @Setter
    private int port;

    @Getter
    @Setter
    private String user;

    @Getter
    @Setter
    private String password;
}
