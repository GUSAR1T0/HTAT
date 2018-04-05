package store.vxdesign.htat.core.connections;

import store.vxdesign.htat.core.exceptions.ConnectionException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public abstract class Connections {
    private final List<AbstractConnection> connections = new ArrayList<>();

    protected <C extends AbstractConnection & Shell, P extends ConnectionProperties> Shell getConnection(Class<C> clazz, P properties) {
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

    public void closeAllConnections() {
        connections.forEach(AbstractConnection::close);
        connections.clear();
    }
}
