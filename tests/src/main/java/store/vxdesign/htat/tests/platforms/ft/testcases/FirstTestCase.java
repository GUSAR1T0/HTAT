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
import store.vxdesign.htat.tests.common.TestExecutionType;
import store.vxdesign.htat.tests.fixtures.FunctionalTestFixture;

@TestEnvironment(platform = TestEnvironment.Platform.FUNCTIONAL_TEST)
@DisplayName("The first functional test case")
public class FirstTestCase implements FunctionalTestFixture {
    @Autowired
    private HostHolder holder;

    @Test
    @TestExecutionType(executionType = TestExecutionType.ExecutionType.CRITICAL)
    @DisplayName("Get host information via SSH")
    public void test01() {
        GeneralHost host = holder.getGeneralHosts().get(0);
        CommandResult info = host.getConnections().getSshConnection().execute(ShellCommand.builder().command("uname").arguments("-a").build());
        System.out.println(info);
        Assertions.assertTrue(!info.getOutput().isEmpty(), "The output is empty");
    }

    @Test
    @TestExecutionType(executionType = TestExecutionType.ExecutionType.CRITICAL)
    @DisplayName("Get list files and directories on host via SSH")
    public void test02() {
        GeneralHost host = holder.getGeneralHosts().get(0);
        CommandResult listFilesAndDirectories = host.getConnections().getSshConnection().execute(ShellCommand.builder().command("ls").arguments("-la").build());
        System.out.println(listFilesAndDirectories);
        Assertions.assertTrue(!listFilesAndDirectories.getOutput().isEmpty(), "The output is empty");
    }

    @Test
    @TestExecutionType(executionType = TestExecutionType.ExecutionType.CRITICAL)
    @DisplayName("Get host information via Telnet")
    public void test03() {
        GeneralHost host = holder.getGeneralHosts().get(0);
        CommandResult info = host.getConnections().getTelnetConnection().execute(ShellCommand.builder().command("uname").arguments("-a").build());
        System.out.println(info);
        Assertions.assertTrue(!info.getOutput().isEmpty(), "The output is empty");
    }

    @Test
    @TestExecutionType(executionType = TestExecutionType.ExecutionType.CRITICAL)
    @DisplayName("Get list files and directories on host via Telnet")
    public void test04() {
        GeneralHost host = holder.getGeneralHosts().get(0);
        CommandResult listFilesAndDirectories = host.getConnections().getTelnetConnection().execute(ShellCommand.builder().command("ls").arguments("-la").build());
        System.out.println(listFilesAndDirectories);
        Assertions.assertTrue(!listFilesAndDirectories.getOutput().isEmpty(), "The output is empty");
    }

    @Test
    @TestExecutionType(executionType = TestExecutionType.ExecutionType.CRITICAL)
    @DisplayName("Change password for test user")
    public void test05() {
        GeneralHost host = holder.getGeneralHosts().get(0);
        ShellCommand command = ShellCommand.builder().
                sudo().
                command("passwd").arguments("testuser").
                expectAndSend("Enter new UNIX password:", "12345678").
                expectAndSend("Retype new UNIX password:", "12345678").
                build();
        CommandResult info = host.getConnections().getSshConnection().execute(command);
        System.out.println(info);
        Assertions.assertTrue(!info.getOutput().isEmpty(), "The output is empty");
    }
}
