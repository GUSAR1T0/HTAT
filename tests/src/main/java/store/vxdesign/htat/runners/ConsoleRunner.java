package store.vxdesign.htat.runners;

import com.beust.jcommander.JCommander;
import org.junit.platform.console.ConsoleLauncher;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "store.vxdesign.htat")
public class ConsoleRunner implements CommandLineRunner {
    @Override
    public void run(String... args) {
        CommandLineArguments parsedCommandLineArguments = new CommandLineArguments();
        JCommander commandLineArguments = new JCommander(parsedCommandLineArguments);
        commandLineArguments.parse(args);
        commandLineArguments.setProgramName("Host Test Automation Tool");
        if (parsedCommandLineArguments.isHelp()) {
            commandLineArguments.usage();
        } else {
            ConsoleLauncher.main(parsedCommandLineArguments.getArguments());
        }
    }

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ConsoleRunner.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
    }
}
