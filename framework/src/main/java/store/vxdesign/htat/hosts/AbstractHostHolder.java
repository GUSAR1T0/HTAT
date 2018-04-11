package store.vxdesign.htat.hosts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import store.vxdesign.htat.core.exceptions.InstanceInitializationException;
import store.vxdesign.htat.core.properties.MergedProperties;
import store.vxdesign.htat.hosts.common.Host;
import store.vxdesign.htat.hosts.common.HostConnections;
import store.vxdesign.htat.hosts.common.HostProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractHostHolder {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final MergedProperties mergedProperties;

    AbstractHostHolder(MergedProperties mergedProperties) {
        this.mergedProperties = mergedProperties;
    }

    <P extends HostProperties, T extends Host<P, ? extends HostConnections>> List<T> initialize(Class<P> propertiesClass, Function<P, T> hostInstanceGenerator) {
        try {
            List<P> propertiesList = mergedProperties.getPropertiesList(propertiesClass);
            return propertiesList.stream().map(hostInstanceGenerator).collect(Collectors.toList());
        } catch (NullPointerException e) {
            logger.trace("List of instances is not set in properties files");
        }

        P properties = mergedProperties.getProperties(propertiesClass);
        if (properties != null) {
            return new ArrayList<>(Collections.singletonList(hostInstanceGenerator.apply(properties)));
        } else {
            logger.trace("Single instance is not set in properties files");
            throw new InstanceInitializationException("Failed to initialize list of instances or single instance: %s.", propertiesClass.getName());
        }
    }

    public abstract void initialize();

    public abstract void stop();
}
