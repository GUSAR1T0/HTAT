package store.vxdesign.htat.runner;

import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import store.vxdesign.htat.core.connections.CommandResult;
import store.vxdesign.htat.core.connections.Connections;
import store.vxdesign.htat.core.connections.Shell;
import store.vxdesign.htat.core.utilities.commands.ShellCommand;

@SpringBootApplication
@ComponentScan(value = "store.vxdesign.htat")
public class MainRunner implements CommandLineRunner {
    private Connections connections;

    public MainRunner(Connections connections) {
        this.connections = connections;
    }

    @Override
    public void run(String... args) {
        Shell shell = connections.getSshConnection();
        CommandResult result = shell.execute(ShellCommand.builder().command("ls").arguments("-l", "-a").build());
        shell.close();
        System.out.println(result);
    }

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(MainRunner.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
    }
}