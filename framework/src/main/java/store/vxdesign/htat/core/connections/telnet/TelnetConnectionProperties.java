package store.vxdesign.htat.core.connections.telnet;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import store.vxdesign.htat.core.connections.ConnectionProperties;

@EqualsAndHashCode(callSuper = true)
@Component
@Getter
@Setter
public class TelnetConnectionProperties extends ConnectionProperties {
}
