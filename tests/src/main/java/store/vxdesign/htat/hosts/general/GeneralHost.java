package store.vxdesign.htat.hosts.general;

import store.vxdesign.htat.hosts.common.AbstractHost;

public class GeneralHost extends AbstractHost<GeneralHostProperties, GeneralHostConnections> {
    public GeneralHost(GeneralHostProperties properties) {
        super(properties, GeneralHostConnections::new);
    }

    @Override
    public GeneralHostProperties getProperties() {
        return properties;
    }

    @Override
    public GeneralHostConnections getConnections() {
        return connections;
    }
}
