## JPA + QueryDSL

- 注意：实体类可以继承聚合根
  - 如果继承则必须在启动类同级创建一个package-info.java文件增加配置

```java
@QueryEntities(value = {BaseJpaAggregate.class})
//这个包名为启动类所在包的名称
package com.hp.jsoup;

import com.hp.jpa.BaseJpaAggregate;
import com.querydsl.core.annotations.QueryEntities;
```
