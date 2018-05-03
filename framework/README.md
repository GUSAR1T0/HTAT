# HTAT.Anemone

## What is the module?

This module is base of all application. All connections, properties and another implementations are located here. Boronia module is based on code from this module.

## Properties

* `hosttype.name` – defines the host name;
* `hosttype.os` – defines the host OS;
* `hosttype.ssh.host` – defines the host address for SSH connection;
* `hosttype.ssh.port` – defines the host port for SSH connection;
* `hosttype.ssh.user` – defines the host user which will perform required actions via SSH connection;
* `hosttype.ssh.password` – defines the host password for user authentication what is set in hosttype.ssh.user;
* `hosttype.ssh.options` – defines the host options for SSH connection;
* `hosttype.telnet.host` – defines the host address for Telnet connection;
* `hosttype.telnet.port` – defines the host port for Telnet connection;
* `hosttype.telnet.user` – defines the host user which will perform required actions via Telnet connection;
* `hosttype.telnet.password` – defines the host password for user authentication what is set in hosttype.telnet.user.

*Notice*: `hosttype` is type of host which implemented and can be used in Boronia module.
