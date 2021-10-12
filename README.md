# java-dek
Java Decompiler CLI based on jd-gui

Our goal is to improve and fix issues of [jd-core](https://github.com/java-decompiler/jd-core). That project was last updated on 2019.

Moreover we provide a simple CLI utility that allow to decompile either a single class file, a folder of class files, or a jar archive.

## Features

* Maven build system
* Clean code
* Javadoc

## Compile and run

Compile with

    mvn package

Run with

    java -jar java-dek.jar [-d <destination root folder>] <sources>

Warning: tested qith Java 8. Some tests fails with Java 11+.

