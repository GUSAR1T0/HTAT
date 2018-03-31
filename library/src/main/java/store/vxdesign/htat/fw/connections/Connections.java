package store.vxdesign.htat.fw.connections;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import store.vxdesign.htat.fw.exceptions.ConnectionException;
import store.vxdesign.htat.fw.properties.MergedProperties;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@Component
@Scope("singleton")
public class Connections {
    private final List<AbstractConnection> connections = new ArrayList<>();
    private final MergedProperties mergedProperties;

    public Connections(MergedProperties mergedProperties) {
        this.mergedProperties = mergedProperties;
    }

    private <C extends AbstractConnection & Shell, P extends ConnectionProperties> Shell getConnection(Class<C> clazz, P properties) {
        Predicate<AbstractConnection> predicate = connection -> clazz.isInstance(connection) && Objects.equals(connection.getProperties(), properties);
        Optional<AbstractConnection> optionalConnection = connections.stream().filter(predicate).findFirst();
        C connection = optionalConnection.isPresent() ? clazz.cast(optionalConnection.get()) : null;

        if (connection == null) {
            try {
                connection = clazz.getConstructor(properties.getClass()).newInstance(properties);
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
                throw new ConnectionException("Failed to create %s instance: %s", clazz.getName(), e);
            }
            connections.add(connection);
        }

        return connection;
    }
}
