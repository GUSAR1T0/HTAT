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
package store.vxdesign.htat.tests.platforms.ft.testcases;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import store.vxdesign.htat.core.connections.CommandResult;
import store.vxdesign.htat.core.utilities.commands.ShellCommand;
import store.vxdesign.htat.hosts.HostHolder;
import store.vxdesign.htat.hosts.general.GeneralHost;
import store.vxdesign.htat.tests.common.TestEnvironment;
import store.vxdesign.htat.tests.common.TestExecution;
import store.vxdesign.htat.tests.fixtures.FunctionalTestFixture;

@DisplayName("The first functional test case")
@TestEnvironment(unitType = TestEnvironment.TestUnitType.SINGLE_CASE)
public class FirstTestCase implements FunctionalTestFixture {
    private static final ShellCommand hostInfo = ShellCommand.builder().command("uname").arguments("-a").build();
    private static final ShellCommand listFilesAndDirectories = ShellCommand.builder().command("ls").arguments("-la").build();

    @Autowired
    private HostHolder holder;

    @Test
    @DisplayName("Get host information via SSH")
    @TestExecution(executionType = TestExecution.ExecutionType.CRITICAL)
    void test01() {
        GeneralHost host = holder.getGeneralHosts().get(0);
        CommandResult commandResult = host.getConnections().getSshConnection().execute(hostInfo);
        System.out.println(commandResult);
        Assertions.assertTrue(!commandResult.getOutput().isEmpty(), "The output is empty");
    }

    @Test
    @DisplayName("Get list files and directories on host via SSH")
    @TestExecution(executionType = TestExecution.ExecutionType.SIMPLE)
    void test02() {
        GeneralHost host = holder.getGeneralHosts().get(0);
        CommandResult commandResult = host.getConnections().getSshConnection().execute(listFilesAndDirectories);
        System.out.println(commandResult);
        Assertions.assertTrue(!commandResult.getOutput().isEmpty(), "The output is empty");
    }

    @Test
    @DisplayName("Get host information via Telnet")
    @TestExecution(executionType = TestExecution.ExecutionType.CRITICAL)
    void test03() {
        GeneralHost host = holder.getGeneralHosts().get(0);
        CommandResult info = host.getConnections().getTelnetConnection().execute(hostInfo);
        System.out.println(info);
        Assertions.assertTrue(!info.getOutput().isEmpty(), "The output is empty");
    }

    @Test
    @DisplayName("Get list files and directories on host via Telnet")
    @TestExecution(executionType = TestExecution.ExecutionType.SIMPLE)
    void test04() {
        GeneralHost host = holder.getGeneralHosts().get(0);
        CommandResult commandResult = host.getConnections().getTelnetConnection().execute(listFilesAndDirectories);
        System.out.println(commandResult);
        Assertions.assertTrue(!commandResult.getOutput().isEmpty(), "The output is empty");
    }

    @Test
    @DisplayName("Change password for test user")
    @TestExecution(executionType = TestExecution.ExecutionType.CRITICAL)
    void test05() {
        GeneralHost host = holder.getGeneralHosts().get(0);
        ShellCommand changePassword = ShellCommand.builder().
                sudo().
                command("passwd").arguments("testuser").
                expectAndSend("Enter new UNIX password:", "12345678").
                expectAndSend("Retype new UNIX password:", "12345678").
                build();
        CommandResult commandResult = host.getConnections().getSshConnection().execute(changePassword);
        System.out.println(commandResult);
        Assertions.assertTrue(!commandResult.getOutput().isEmpty(), "The output is empty");
    }
}
