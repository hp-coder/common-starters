## JPA + QueryDSL 集成 starter

- 注意：实体类可以集成统一聚合根
  - 如果继承则必须在启动类同级创建一个package-info.java文件增加配置
```java
@QueryEntities(value = {BaseJpaAggregate.class})
package com.hp.jsoup;

import com.hp.jpa.BaseJpaAggregate;
import com.querydsl.core.annotations.QueryEntities;
```