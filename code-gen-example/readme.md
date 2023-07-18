Note

It has been tested when using JDK17, the apt-maven plugin is not needed to generate .java files in the target/generated-source directory.

In a JDK11 compile environment, the apt-maven plugin is still needed, otherwise, the Q class of the QueryDsl can't be generated properly.

Usage

```shell
mvn clean
mvn install -pl com.hp:code-gen-common-spring-boot-starter -am -Dtest.skip=true
mvn compile -pl com.hp:test-code-gen-module -am -Dtest.skip=true
```
Debug

debug通过idea等编译器的maven插件运行时的debug模式来做调试
