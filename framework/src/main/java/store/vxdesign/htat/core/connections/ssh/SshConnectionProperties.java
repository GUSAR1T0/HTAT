package store.vxdesign.htat.core.connections.ssh;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import store.vxdesign.htat.core.connections.ConnectionProperties;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Component
public class SshConnectionProperties extends ConnectionProperties {
    @Getter
    @Setter
    private Map<String, String> options;
}
