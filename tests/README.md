# HTAT.Boronia

## What is the module?

This module launches tests and makes reports about results. You can create test scenarios for some kinds of test platforms in this part of project. Also properties files for test performing are located here.

## VM arguments

VM arguments are paths to properties file, logging level for application, path for logging.

* `-Dproperties=path/to/main.properties` - this argument is needed to set for application some properties for remote connections (*example of properties is put below*).
* `-Dlog.level.application=[off|info|debug|warn|error|fatal|trace|all]` - this argument is needed to set a logging level for application component.
* `-Dlog.level.spring=[off|info|debug|warn|error|fatal|trace|all]` - this argument is needed to set a logging level only for Spring Boot dependency.
* `-Dlog.path=path/to/logs` - this argument is needed to set a path for log directory saving.

## Application arguments

Application arguments are help menu and classpath to test case or suite.

* `-h` or `--help` - this argument is needed to show all application arguments.
* `-p=[ft|st]` or `--test-platform=[ft|st]` - this argument set a test platform.
* `-ut=[case|suite]` or `--test-unit-type=[case|suite]` - this argument set a type of test scenario.
* `-it=[method|class|package]` or `--test-instance-type=[method|class|package]` - this argument set a type of test instance.
* `-t=...` or `--test-name=...` - this argument set a name of test what should be executed.

**Example:** `-p=ft -ut=case -it=class -t=FirstTestCase` or `-p=ft -ut=suite -it=class -t=FirstTestSuite`.

## Example of properties

This module has implemented GeneralHost instance. By this reason, `hosttype` is `general` here.

```
default_host=some-host.ru
default_user=testuser
default_password=password

general.name=some-host
general.os=unix

general.ssh.host=${default_host}
general.ssh.port=${ssh.default_port}            // 22
general.ssh.user=${default_user}
general.ssh.password=${default_password}
general.ssh.options[StrictHostKeyChecking]=no

general.telnet.host=${default_host}
general.telnet.port=${telnet.default_port}      // 23
general.telnet.user=${default_user}
general.telnet.password=${default_password}
```

This description of all fields is presented in Anemone/README.md file.
