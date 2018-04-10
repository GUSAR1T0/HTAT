package store.vxdesign.htat.hosts;

import lombok.Getter;
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

    private void initializeGeneralHosts() {
        stopGeneralHosts();
        generalHosts.addAll(initialize(GeneralHostProperties.class, GeneralHost::new));
    }

    private void stopGeneralHosts() {
        generalHosts.forEach(generalHost -> generalHost.getConnections().closeAllConnections());
        generalHosts.clear();
    }
}
