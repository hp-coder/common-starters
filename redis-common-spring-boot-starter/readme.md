# Redis 插件

> 针对 spring-boot-starter-redis的依赖做处理

## Features

### 1. Standalone mode 切库操作

> 集群和sentinel方式了解不多暂时没处理

#### Usage
1. 引入依赖

    ```xml
    <dependency>
      <groupId>com.hp</groupId>
      <artifactId>redis-common-spring-boot-starter</artifactId>
      <version>1.0.0-SNAPSHOT</version>
    </dependency>
    ```
   
2. 选择链接池, 选jedis则增加jedis依赖即可, 无需排除lettuce

    ```xml
    <dependency>
      <groupId>redis.clients</groupId>
      <artifactId>jedis</artifactId>
    </dependency>
    ```
   
3. 配置SpringBoot配置项

    ```yaml
    spring:
      data:
        redis:
          client-type: lettuce # 明确指定使用的客户端
          database: 0 # 保留主库, 不写默认0, 默认Spring自己的starter也会创建一个对应库的template
          port: 10600
          host: 192.168.0.192
          password: root
          connect-timeout: 2000ms
          timeout: 2000ms
          multiple: true # 开启多库配置
          databases:  # 多库配置
            - 1
            - 2
            - 3
    ```

#### Notes

- 20240423
  - issue 1:

        原意是想保留原生默认注入一个redisTemplate供未引入插件的项目保持功能正常, 但是由于原生的AutoConfigurer配置会出现多个同类Bean冲突,
        并且又不想配置允许覆盖Bean的操作, 所以第一版暂时需要通过排除`RedisAutoConfiguration`的方式注入, 并通过插件自定义自动配置类注入一个
        默认的`RedisTemplate`和`StringRedisTemplate`.

  - issue 2:
    
        在注册bean时, 仅仅使用了hutools的registerBean API, 导致在例如实现了SmartInitializingSingleton, EnvironmentAware之类的接口
        时, 无法通过构造器直接注入到业务Bean中, 这个不是百分之一百确定, 所以目前时间有限, 暂时通过hutools的 getBeanOfType API 获取, 并通过
        一个工具类开放接口获取不同库的实例

- 20240424
  - issue 1: resolved? 

        排除RedisAutoConfiguration时,正常注入自定义BEAN; 不排除时, 自动配置类会通过配置文件创建一个RedisConnectionDetails, 由于插件使用
        底层的配置 RedisStandaloneConfiguration, 在实际创建工厂时, 原生自动配置加载早于自定义配置, 而且自定义配置不是details, 导致原生配置
        通过details自动创建了一个默认的 template Bean, 后续自定义配置创建时, 能先后获取到一个原生Details和动态多个StandAlone配置
      
        工厂配置类的父类构造也可以使用details配置, 导致会通过自定义配置重复创建一个和原生template同一个库的template. 这个保留.        

        最终凑巧保留了原生的template的同时, 也动态创建了多个库的template, 综上, 因原生template早于自定义的, 在创建mapping的时候 使用 putIfAbsent 策略.
    
        但后续可能还是会考虑通过Details类创建解析配置文件. 不确定, 还得研究研究.
  
  - issue 2: 凑合用先
      
        原生template保留后, 可以直接在工具类的缓存中获取到.

- 202404025
  - usage notes:
    
        因为lettuce自动配置上的@ConditionalOnProperties对配置项的处理是缺省也是TRUE, 所以目前客户端需要手动指定
        
        spring.data.redis.client-type=jedis

        来禁用lettuce自动配置

- 20240429
  - usage notes: 
    
        依赖处理: 不排除lettuce, 在配置中指明使用 jedis 并增加jedi依赖, 即可切换链接库
