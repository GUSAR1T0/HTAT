package store.vxdesign.htat.runner;

import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import store.vxdesign.htat.core.utilities.commands.ShellCommand;
import store.vxdesign.htat.hosts.HostHolder;
import store.vxdesign.htat.hosts.general.GeneralHost;

@SpringBootApplication
@ComponentScan(value = "store.vxdesign.htat")
public class MainRunner implements CommandLineRunner {
    private final HostHolder holder;

    public MainRunner(HostHolder holder) {
        this.holder = holder;
    }

    @Override
    public void run(String... args) {
        holder.initialize();
        GeneralHost host = holder.getGeneralHosts().get(0);
//        System.out.println(host.getProperties().getName());
        host.getConnections().getSshConnection().execute(ShellCommand.builder().command("uname").arguments("-a").build());
        host.getConnections().getSshConnection().execute(ShellCommand.builder().command("ls").arguments("-la").build());
        host.getConnections().getTelnetConnection().execute(ShellCommand.builder().command("uname").arguments("-a").build());
        host.getConnections().getTelnetConnection().execute(ShellCommand.builder().command("ls").arguments("-la").build());
        holder.stop();
    }

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(MainRunner.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
    }
}