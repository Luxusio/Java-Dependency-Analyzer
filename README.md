# Java-Dependency-Analyzer

## Introduction

This project analyzes method-level dependency graph using ASM library for a given directory of Java source files.  
Not tested for other languages like groovy and kotlin.  
This project is made for learning how java bytecode works.

If you want to print method-level dependency, I recommend to use IDE features.

- [IntelliJ IDEA Call Hierarchy](https://www.jetbrains.com/help/idea/viewing-structure-and-hierarchy-of-the-source-code.html) (Ctrl + Alt + H)
- [Eclipse Call Hierarchy](https://help.eclipse.org/latest/index.jsp?topic=%2Forg.eclipse.jdt.doc.user%2Freference%2Fviews%2Fref-call-hierarchy.htm) (Ctrl + Alt + H)

## How to use

This project requires java 17

Download jar file in [Releases](https://github.com/Luxusio/Java-Dependency-Analyzer/releases)

```shell
java -jar <programFileName> <jarPath> <packagePrefix> <classPath> <methodName>
```
- programFileName: program jar file name. (i.e. jda.jar)
- jarPath: path to jar file to be analyzed. (i.e. ./SampleJar/build/libs)
- packagePrefix: package prefix to analyze. (i.e. io/luxus/sample/include)
- classPath: class path to analyze. (i.e. io/luxus/sample/include/MyVisibleRepository)
- methodName: method name to analyze. (i.e. doSomething)

### Example

```shell
java -jar jda.jar SampleJar/build/libs io/luxus/sample/include io/luxus/sample/include/MyVisibleRepository doSomething()
```

## How it works

Read comments in `findDependencies` method in [Main.kt](src/main/kotlin/io/luxus/jda/Main.kt) file.

If you want to learn by debugging project,
create jar file of SampleJar module and run in debug mode
from [MainKtTest.findDependencies](src/test/kotlin/io/luxus/jda/MainKtTest.kt) test code.

create jar file of SampleJar

```shell
./gradlew SampleJar:jar
```
