package store.vxdesign.htat.core.properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import store.vxdesign.htat.core.utilities.common.Utils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

@Component
@Scope("singleton")
public class MergedProperties {
    private final Logger logger = LogManager.getLogger(this.getClass());

    private Environment environment;

    public MergedProperties() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(DefaultProperties.class);
        ctx.register(DeclaredProperties.class);
        ctx.refresh();
        this.environment = ctx.getEnvironment();
    }

    private <O, P extends Properties, B extends Bindable<O>> O getProperties(Class<P> clazz, String getterType, Supplier<B> function) {
        String prefix = Utils.getClassPrefix(clazz);
        logger.trace("Search the '{}' properties by '{}' prefix", getterType, prefix);
        return Binder.get(environment).bind(prefix, function.get()).orElse(null);
    }

    public <P extends Properties> P getProperties(Class<P> clazz) {
        return getProperties(clazz, "single", () -> Bindable.of(clazz));
    }

    public <P extends Properties> List<P> getPropertiesList(Class<P> clazz) {
        return getProperties(clazz, "list", () -> Bindable.listOf(clazz));
    }

    public <P extends Properties> Set<P> getPropertiesSet(Class<P> clazz) {
        return getProperties(clazz, "set", () -> Bindable.setOf(clazz));
    }

    public <P extends Properties> Map<String, P> getPropertiesMap(Class<P> clazz) {
        return getProperties(clazz, "map", () -> Bindable.mapOf(String.class, clazz));
    }
}