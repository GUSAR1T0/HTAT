/*
 * Copyright (c) 2018 Roman Mashenkin <xromash@vxdesign.store>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package store.vxdesign.htat.core.utilities.writers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import store.vxdesign.htat.core.connections.AbstractConnection;
import store.vxdesign.htat.core.connections.CommandResult;
import store.vxdesign.htat.core.connections.ConnectionProperties;
import store.vxdesign.htat.core.utilities.common.ReflectionUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;

public class CommandResultWriter extends AbstractWriter {
    private CommandResultWriter(String directoryPath, String logFileName) {
        super(directoryPath, logFileName);
    }

    public static <P extends ConnectionProperties> CommandResultWriter create(AbstractConnection<P> connection) {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration configuration = context.getConfiguration();
        String directoryPath = configuration.getStrSubstitutor().getVariableResolver().lookup("DIRECTORY_PATH");
        String connectionsDirectoryPath = configuration.getStrSubstitutor().getVariableResolver().lookup("CONNECTIONS_DIRECTORY_PATH").
                replaceFirst("\\$\\{DIRECTORY_PATH}", directoryPath);
        String connectionClass = ReflectionUtils.getClassPrefix(connection.getClass());
        String connectionLogFileName = String.format("%s_%s_%s_%d.log", connectionClass,
                connection.getProperties().getUser(), connection.getProperties().getHost(),
                connection.getProperties().getPort());
        return new CommandResultWriter(connectionsDirectoryPath, connectionLogFileName);
    }

    public void write(CommandResult commandResult) {
        if (!directoryPath.exists()) {
            directoryPath.mkdirs();
        }

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
            logger.error("Failed to write in {} file current command result:\n{}", logFile, commandResult);
        }
    }
}
