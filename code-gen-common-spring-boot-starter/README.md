# 自定义代码生成器

> 目前如果根据 only4play 那种形式编写，实际也是硬编码只能生成 JPA 相关的代码，以及及其贴近其项目架构的基本类

- 使用 SPI 机制 + Google AutoService获取实现类
- 使用 JavaPoet 编排具体类的内容
- 对于JPA架构下的项目, Mapper将作为MapStruct功能的实现,与Mybatis的Mapper概念存在冲突, 为了兼容处理, 对于mybatis中的Mapper概念将在repository包中处理
- 如果需要前端代码则要利用模版引擎去做
- 对于后期不需根据业务修改内容的类可以直接生成到target下

processor包下的各类具体要如何生成，生成什么样的文件等，都需要根据实际项目情况确定，最好是项目都基于某些规定，在此规定下再进行代码生成器的开发

还是期望能形成通用模块，例如同时能配置JPA和Mybatis等

引入模块maven坐标， mvn clean compile 加自动根据注解解析生成相关类文件；
`注：可能需要将target/generated-source/annotations标记为generated sources root`