package store.vxdesign.htat.hosts;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import store.vxdesign.htat.core.properties.MergedProperties;
import store.vxdesign.htat.hosts.general.GeneralHost;
import store.vxdesign.htat.hosts.general.GeneralHostProperties;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope("singleton")
public class HostHolder extends AbstractHostHolder {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Getter
    private final List<GeneralHost> generalHosts = new ArrayList<>();

    public HostHolder(MergedProperties mergedProperties) {
        super(mergedProperties);
    }

    @Override
    public void initialize() {
        initializeGeneralHosts();
        logger.debug("The host holder was initialized successfully");
    }

    @Override
    public void stop() {
        stopGeneralHosts();
        logger.debug("The host holder was stopped successfully");
    }

    private void initializeGeneralHosts() {
        logger.trace("Stopping the possible previous initialized general hosts list");
        stopGeneralHosts();
        logger.trace("Initializing general hosts list");
        generalHosts.addAll(initialize(GeneralHostProperties.class, GeneralHost::new));
    }

    private void stopGeneralHosts() {
        logger.trace("Closing the open connections from general hosts list");
        generalHosts.forEach(generalHost -> generalHost.getConnections().closeAllConnections());
        generalHosts.clear();
    }
}
