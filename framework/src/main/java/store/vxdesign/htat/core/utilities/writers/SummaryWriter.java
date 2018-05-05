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

import io.bretty.console.tree.TreePrinter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import store.vxdesign.htat.tests.common.TestTreeNode;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class SummaryWriter extends AbstractWriter {
    private SummaryWriter(String directoryPath) {
        super(directoryPath, "summary.log");
    }

    public static SummaryWriter create() {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration configuration = context.getConfiguration();
        String directoryPath = configuration.getStrSubstitutor().getVariableResolver().lookup("DIRECTORY_PATH");
        String logsDirectoryPath = configuration.getStrSubstitutor().getVariableResolver().lookup("LOGS_DIRECTORY_PATH").
                replaceFirst("\\$\\{DIRECTORY_PATH}", directoryPath);
        return new SummaryWriter(logsDirectoryPath);
    }

    public void write(TestTreeNode tree, TestExecutionSummary summary) {
        try (FileOutputStream fos = new FileOutputStream(logFile, true);
             PrintWriter summaryWriter = new PrintWriter(fos);
             PrintWriter consoleWriter = new PrintWriter(System.out)) {
            String treeString = TreePrinter.toString(tree);
            consoleWriter.println();
            write(consoleWriter, treeString, summary);
            write(summaryWriter, treeString, summary);
        } catch (IOException e) {
            logger.error("Failed to write in {} file summary of test results", logFile);
        }
    }

    private void write(PrintWriter writer, String treeString, TestExecutionSummary summary) {
        writer.println(treeString);
        summary.printTo(writer);
        summary.printFailuresTo(writer);
    }
}
