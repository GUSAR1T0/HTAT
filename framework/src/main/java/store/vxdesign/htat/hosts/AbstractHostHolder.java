package store.vxdesign.htat.hosts;

import store.vxdesign.htat.core.properties.MergedProperties;

public abstract class AbstractHostHolder {
    final MergedProperties mergedProperties;

    public AbstractHostHolder(MergedProperties mergedProperties) {
        this.mergedProperties = mergedProperties;
    }

    public abstract void initialize();

    public abstract void stop();
}
