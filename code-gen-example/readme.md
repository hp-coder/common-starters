Usage

1. mvn clean
2. mvn install -pl com.hp:code-gen-common-spring-boot-starter -am -Dtest.skip=true
3. mvn compile -pl com.hp:test-code-gen-module -am -Dtest.skip=true

Note

By default, this module generates codes with Spring Data Jpa.

To generate codes that adhere to the Mybatis-plus framework, passing the argument `-Aorm=mbp` when calling the compilation process.

1. mvn compile -pl com.hp:test-code-gen-module -am -Dtest.skip=true -Aorm=mbp 

Debug

- debug通过idea等编译器的maven插件运行时的debug模式来做调试
