# jd-cli
Java Decompiler CLI based on jd-gui

Our goal is to decompile class files up to Java 8.

This project is based on https://github.com/java-decompiler/jd-core, which appears to be last updated on 2019

Compile with

    mvn package

Run with

    java -jar jd-cli.jar [-d <destination root folder>] <classes root folder>

