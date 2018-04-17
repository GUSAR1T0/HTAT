package store.vxdesign.htat.core.utilities.writers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import store.vxdesign.htat.core.connections.AbstractConnection;
import store.vxdesign.htat.core.connections.CommandResult;
import store.vxdesign.htat.core.connections.ConnectionProperties;
import store.vxdesign.htat.core.utilities.common.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;

public class CommandResultWriter {
    private final Logger logger = LogManager.getLogger(this.getClass());
    private final File connectionDirectoryPath;
    private final File connectionLogFileName;

    private CommandResultWriter(String connectionDirectoryPath, String connectionLogFileName) {
        this.connectionDirectoryPath = new File(connectionDirectoryPath);
        this.connectionLogFileName = new File(connectionLogFileName);
    }

    public static <P extends ConnectionProperties> CommandResultWriter create(AbstractConnection<P> connection) {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration configuration = context.getConfiguration();
        String directoryPath = configuration.getStrSubstitutor().getVariableResolver().lookup("DIRECTORY_PATH");
        String connectionsDirectoryPath = configuration.getStrSubstitutor().getVariableResolver().lookup("CONNECTIONS_DIRECTORY_PATH").
                replaceFirst("\\$\\{DIRECTORY_PATH}", directoryPath);
        String connectionClass = Utils.getClassPrefix(connection.getClass());
        String connectionLogFileName = String.format("%s_%s_%s_%d.log", connectionClass,
                connection.getProperties().getUser(), connection.getProperties().getHost(),
                connection.getProperties().getPort());
        return new CommandResultWriter(connectionsDirectoryPath, connectionLogFileName);
    }

    public void write(CommandResult commandResult) {
        if (!connectionDirectoryPath.exists()) {
            connectionDirectoryPath.mkdirs();
        }

        File logFile = new File(connectionDirectoryPath.getAbsoluteFile() + "/" + connectionLogFileName.getName());
        try (FileOutputStream fos = new FileOutputStream(logFile, true)) {
            String delimiter = String.format("* %s *", String.join("", Collections.nCopies(200, "-")));
            String stringToLog = delimiter +
                    System.lineSeparator() +
                    commandResult +
                    System.lineSeparator() +
                    delimiter +
                    System.lineSeparator();
            fos.write(stringToLog.getBytes());
        } catch (IOException e) {
            logger.error("Failed to write a command result in {}", logFile);
        }
    }
}
