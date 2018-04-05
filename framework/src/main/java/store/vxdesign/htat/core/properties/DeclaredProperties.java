package store.vxdesign.htat.core.properties;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:./${properties:}")
class DeclaredProperties {
}
