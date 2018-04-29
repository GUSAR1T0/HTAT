package store.vxdesign.htat.core.properties;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:${properties:}", ignoreResourceNotFound = true)
class DeclaredProperties {
}
