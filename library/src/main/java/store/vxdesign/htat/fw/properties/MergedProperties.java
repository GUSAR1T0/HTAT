package store.vxdesign.htat.fw.properties;

import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

@Component
@Scope("singleton")
public class MergedProperties {
    private Environment environment;

    public MergedProperties() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.register(DefaultProperties.class);
        ctx.register(DeclaredProperties.class);
        ctx.refresh();
        this.environment = ctx.getEnvironment();
    }

    private <P extends Properties> String getPrefix(Class<P> clazz) {
        try {
            return (String) clazz.getDeclaredField("prefix").get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return clazz.getSimpleName().split("(?=\\p{Upper})")[0].toLowerCase();
        }
    }

    private <O, P extends Properties, B extends Bindable<O>> O getProperties(Class<P> clazz, Supplier<B> function) {
        return Binder.get(environment).bind(getPrefix(clazz), function.get()).orElse(null);
    }

    public <P extends Properties> P getProperties(Class<P> clazz) {
        return getProperties(clazz, () -> Bindable.of(clazz));
    }

    public <P extends Properties> List<P> getPropertiesList(Class<P> clazz) {
        return getProperties(clazz, () -> Bindable.listOf(clazz));
    }

    public <P extends Properties> Set<P> getPropertiesSet(Class<P> clazz) {
        return getProperties(clazz, () -> Bindable.setOf(clazz));
    }

    public <P extends Properties> Map<String, P> getPropertiesMap(Class<P> clazz) {
        return getProperties(clazz, () -> Bindable.mapOf(String.class, clazz));
    }
}
