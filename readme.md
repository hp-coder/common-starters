# Custom spring plugins

The way of using `META-INF/spring.factories` to configure beans is abolished
after the version of SpringBoot 2.7.x.

A new way is to create a file called `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
and put the full class names of the beans that need to be auto-configured in the file.

Auto-configuration 

```shell
META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
```
