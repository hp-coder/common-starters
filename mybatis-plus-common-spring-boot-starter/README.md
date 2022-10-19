针对DDD思想封装的更偏向面向对象的方式封装mybatis-plus组件
- jpa + querydsl 也可以通过该方式改造
- 该starter不影响项目中原有mybatisplus的配置等，仅仅做了一个再次封装
- UpdateHandler.update()方法主要期望能通过抽象及面向对象的思想集成到对象中形成对象的具体行为（方法）而非一味set