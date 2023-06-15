# 插件

> Spring Boot 2.7.x之后废除了 META-INF/spring.factories 的装载方式

调整为

```shell
META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
```
