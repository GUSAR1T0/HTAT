package store.vxdesign.htat.hosts;

import lombok.Getter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import store.vxdesign.htat.core.exceptions.InitializationException;
import store.vxdesign.htat.core.properties.MergedProperties;
import store.vxdesign.htat.hosts.common.Host;
import store.vxdesign.htat.hosts.common.HostConnections;
import store.vxdesign.htat.hosts.common.HostProperties;
import store.vxdesign.htat.hosts.general.GeneralHost;
import store.vxdesign.htat.hosts.general.GeneralHostProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Scope("singleton")
public class HostHolder extends AbstractHostHolder {
    @Getter
    private final List<GeneralHost> generalHosts = new ArrayList<>();

    public HostHolder(MergedProperties mergedProperties) {
        super(mergedProperties);
    }

    @Override
    public void initialize() {
        initializeGeneralHosts();
    }

    @Override
    public void stop() {
        stopGeneralHosts();
    }

    private <P extends HostProperties, T extends Host<P, ? extends HostConnections>> List<T> initialize(Class<P> propertiesClass, Function<P, T> hostInstanceGenerator) {
        try {
            List<P> propertiesList = mergedProperties.getPropertiesList(propertiesClass);
            return propertiesList.stream().map(hostInstanceGenerator).collect(Collectors.toList());
        } catch (NullPointerException e) {
            // TODO: add log message
        }

        P properties = mergedProperties.getProperties(propertiesClass);
        if (properties != null) {
            return new ArrayList<>(Collections.singletonList(hostInstanceGenerator.apply(properties)));
        } else {
            throw new InitializationException("Failed to initialize list or single %s instances.", propertiesClass.getName());
        }
    }

    private void initializeGeneralHosts() {
        stopGeneralHosts();
        generalHosts.addAll(initialize(GeneralHostProperties.class, GeneralHost::new));
    }

    private void stopGeneralHosts() {
        generalHosts.forEach(generalHost -> generalHost.getConnections().closeAllConnections());
        generalHosts.clear();
    }
}
