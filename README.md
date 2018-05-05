# HTAT (Host Test Automation Tool) -> [![Build Status](https://travis-ci.org/GUSAR1T0/HTAT.svg?branch=master)](https://travis-ci.org/GUSAR1T0/HTAT)

## What is the project?

The main purpose of project is creating a application that can perform testing of some remote devices via network protocols (SSH and Telnet).
HTAT is multi module Java-based project that is handled by Gradle Build Tool: `Anemone|framework` and `Boronia|tests`.

## Base of project

Java SE 8 was used for this project. Moreover, HTAT uses the following libraries:
* Spring Boot 2 with test module;
* JUnit 5;
* Project Lombok;
* Apache Log4j2;
* JSch;
* Apache Commons Net;
* JCommander.

## How to build locally

HTAT project is managed via Gradle Build Tool. If you want to build the project, firstly, you should clone it:

```
~> git clone https://github.com/GUSAR1T0/HTAT.git
```

The next step is Gradle wrapper calling and do something like that:

```
~> cd HTAT
~/HTAT> ./gradlew clean build
```

If wrapper doesn't work, possibly you should give some rights for file (*it will work on Unix and Mac systems*):

```
~/HTAT> chmod +x gradlew
```

After that you can find executable JAR file in **~/HTAT/Boronia/build/libs** - **Boronia-VERSION.jar**.

## How to use

When you built and prepared the JAR file, you can perform testing:

```
~/HTAT> java -jar [VM arguments] Boronia/build/libs/Boronia-[VERSION].jar [application arguments]
```

*Notice*: full information about arguments is described into Boronia/README.md file.

## License

This code is under the [MIT license](https://mit-license.org/).
