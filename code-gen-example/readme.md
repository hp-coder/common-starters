1. mvn clean
2. mvn install -pl com.luban:code-gen-common-spring-boot-starter -am -Dtest.skip=true
3. mvn compile -pl com.luban:test-code-gen-module -am -Dtest.skip=true

- debug通过idea等编译器的maven插件运行时的debug模式来做调试
