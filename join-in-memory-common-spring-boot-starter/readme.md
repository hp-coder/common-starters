# Join In Memory 框架使用文档

## 框架概述

Join In Memory 是一个基于内存的关联查询框架，旨在简化对象之间的关联数据填充操作。通过注解驱动的方式，自动完成对象关联属性的数据装载，避免手动进行多次数据库查询。

## 业务逻辑

### 核心功能
- **字段关联查询**：通过 JoinInMemory 注解自动填充对象的关联属性
- **方法后置处理**：通过 AfterJoin 注解在关联查询完成后执行后置处理逻辑
- **返回值自动填充**：通过 JoinAtReturn 注解自动对方法返回值进行关联查询填充

### 工作流程
1. 解析标注了 JoinInMemory 注解的字段
2. 提取关联键值并批量查询关联数据
3. 建立映射关系并填充到目标字段
4. 执行 AfterJoin 标注的方法

## 使用方法

### 1. 字段关联查询

首先创建一个自定义注解，提高复用性：

```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JoinInMemory(
        keyFromSourceData = "",
        keyFromJoinData = "id",
        loader = "@userService.findByIds(#root)",
        joinDataConverter = "#this.toVO()"
)
public @interface JoinUser {
    @AliasFor(annotation = JoinInMemory.class, attribute = "keyFromSourceData")
    String keyFromSourceData();
    
    @AliasFor(annotation = JoinInMemory.class, attribute = "runLevel")
    ExecuteLevel runLevel() default ExecuteLevel.FIFTH;
}
```


然后在实体类中使用：

```java
@JoinInMemoryConfig(executorType = JoinInMemoryExecutorType.PARALLEL)
public class OrderVO {
    
    private Long userId;
    private Long creatorId;
    
    @JoinUser(keyFromSourceData = "userId")
    private UserVO user;
    
    @JoinUser(keyFromSourceData = "creatorId")
    private UserVO creator;
}
```


### 2. 方法后置处理

```java
public class OrderVO {
    
    private String status;
    private String statusDesc;
    
    @AfterJoin
    public void processStatus() {
        this.statusDesc = getStatusDescription(status);
    }
}
```


### 3. 返回值自动填充

```java
@Service
public class OrderService {
    
    @JoinAtReturn("orders")  // 从返回值中提取 orders 字段进行关联查询
    public Map<String, Object> getOrderList() {
        // 返回包含 orders 的 Map
        return result;
    }
}
```


## 配置方法

### 1. 基础配置

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,join-in-memory
  metrics:
    export:
      prometheus:
        enabled: true
  endpoint:
    metrics:
      enabled: true
    join-in-memory:
      enabled: true
```


### 2. 执行器配置

```java
@JoinInMemoryConfig(
    executorType = JoinInMemoryExecutorType.PARALLEL,  // 并行执行
    executorName = "defaultJoinInMemoryExecutor",      // 线程池名称
    fieldProcessPolicy = JoinInMemoryExecutorType.GROUPED  // 字段分组策略
)
public class OrderVO {
    // ...
}
```


### 3. 高级配置

```java
// 自定义线程池
@Bean
public ExecutorService customJoinExecutor() {
    return new ThreadPoolExecutor(
        10, 50, 60L, TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(200)
    );
}

// 配置使用自定义线程池
@JoinInMemoryConfig(executorName = "customJoinExecutor")
public class CustomVO {
    // ...
}
```


## 注解详解

### JoinInMemory
- keyFromSourceData: 从源对象提取关联键的 SpEL 表达式
- keyFromJoinData: 从关联对象提取键的 SpEL 表达式
- loader: 数据加载方法的 SpEL 表达式
- joinDataConverter: 数据转换的 SpEL 表达式
- runLevel: 执行优先级

### JoinInMemoryConfig
- executorType: 执行类型（串行/并行）
- executorName: 线程池 Bean 名称
- fieldProcessPolicy: 字段处理策略

### AfterJoin
- runLevel: 执行优先级

### JoinAtReturn
- value: 从返回值提取数据的 SpEL 表达式

## 性能优化

### 1. 执行策略选择
- **串行执行**：适用于简单场景，避免线程开销
- **并行执行**：适用于复杂关联，提升查询效率

### 2. 字段分组策略
- **分组处理**：相同配置的字段合并查询，减少数据库访问
- **独立处理**：每个字段独立查询，灵活性更高

### 3. 缓存机制
- 框架自动缓存执行器实例，避免重复解析
- 合理配置线程池大小，避免资源浪费

## 监控指标

### 暴露的指标
- join.executor.cache.size: 执行器缓存大小
- join.executions: 关联查询执行次数
- join.execution.time: 执行时间统计

### 监控端点
- /actuator/join-in-memory: 缓存状态信息
- /actuator/metrics: 指标详情

## 最佳实践

1. **合理使用执行策略**：根据业务复杂度选择串行或并行
2. **优化 SpEL 表达式**：确保表达式性能和准确性
3. **避免循环依赖**：防止关联查询的循环引用
4. **监控性能指标**：关注执行时间和资源消耗
