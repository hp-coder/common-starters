# 自定义代码生成器

## 介绍

- 使用 SPI 机制 + Google AutoService获取实现类
- 使用 JavaPoet 编排具体类的内容
- 对于JPA架构下的项目, Mapper将作为MapStruct功能的实现,与Mybatis的Mapper概念存在冲突, 为了兼容处理, 对于mybatis中的Mapper概念将在repository包中处理
- 对于后期不需根据业务修改内容的类可以直接生成到target下

## 开发

### 1. 说明
由于使用的是在java编译时编排代码的方式, 如果是将类生成到`/target`目录下

    例如模块中的VO, DTO类, 由于设计时此类直接对应数据模型不可修改, 所以生成到 /target/generated-source 中

此目录下数据将被编译器和maven视为已经编译过, 不会二次编译

所以不能添加需要被编译后再被引用的元素, 例如lombok的`@Data`等注解

此时如果在上述目录下生成类带有此类注解, lombok并不会编译注解为对应方法

最终将导致MapStruct在编译后生成实现类时, 无法找到对应属性的`getter`, `setter`方法, 导致转换时数据的丢失.

### 2. 依赖
mapstruct 和 lombok 能用最新就最新

google的auto包目前已经满足需求, 但是由于编译时不想再引入特殊类型转换的注解, 在写request和response对象的时候用了其中的MoreTypes工具类
导致改包的引用不能再为optional, 影响不大, 但如果调整需要注意.

## 使用

### 1. 依赖

依赖及配置

编译时有严格顺序要求: 代码生成器->lombok编译->mapstruct编译->lombok和mapstruct的处理
```xml
<properties>
    <mapstruct.version>1.5.3.Final</mapstruct.version>
    <lombok-mapstruct-binding.version>0.2.0</lombok-mapstruct-binding.version>
    <code-gen-version>1.0.1-SNAPSHOT</code-gen-version>
    <lombok.version>1.18.16</lombok.version>
</properties>

<dependencies>

<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>${mapstruct.version}</version>
</dependency>

<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct-processor</artifactId>
    <version>${mapstruct.version}</version>
</dependency>

<!-- lombok dependencies should not end up on classpath -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
</dependency>

<dependency>
    <groupId>com.hp</groupId>
    <artifactId>code-gen-common-spring-boot-starter</artifactId>
    <version>${code-gen-version}</version>
</dependency>

</dependencies>

<build>
<plugins>
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.8.1</version>
        <configuration>
            <source>1.8</source>
            <target>1.8</target>
            <encoding>UTF-8</encoding>
            <compilerArgument>-Xlint:unchecked</compilerArgument>
            <annotationProcessorPaths>
                <path>
                    <groupId>com.hp</groupId>
                    <artifactId>code-gen-common-spring-boot-starter</artifactId>
                    <version>${code-gen-version}</version>
                </path>
                <path>
                    <groupId>org.projectlombok</groupId>
                    <artifactId>lombok</artifactId>
                    <version>${lombok.version}</version>
                </path>
                <path>
                    <groupId>org.mapstruct</groupId>
                    <artifactId>mapstruct-processor</artifactId>
                    <version>${mapstruct.version}</version>
                </path>
                <path>
                    <groupId>org.projectlombok</groupId>
                    <artifactId>lombok-mapstruct-binding</artifactId>
                    <version>${lombok-mapstruct-binding.version}</version>
                </path>
            </annotationProcessorPaths>
        </configuration>
    </plugin>
    <plugin>
        <groupId>com.mysema.maven</groupId>
        <artifactId>apt-maven-plugin</artifactId>
        <version>1.1.3</version>
        <executions>
            <execution>
                <id>querydsl</id>
                <goals>
                    <goal>process</goal>
                </goals>
                <configuration>
                    <outputDirectory>target/generated-sources/java</outputDirectory>
                    <processor>com.querydsl.apt.jpa.JPAAnnotationProcessor</processor>
                </configuration>
            </execution>
        </executions>
    </plugin>
</plugins>
</build>
```

### 2. Entity模版

> live template

```shell
import com.$PACK$.codegen.processor.controller.GenController;
import com.$PACK$.codegen.processor.dto.GenDto;
import com.$PACK$.codegen.processor.mapper.GenMapper;
import com.$PACK$.codegen.processor.repository.GenRepository;
import com.$PACK$.codegen.processor.request.GenRequest;
import com.$PACK$.codegen.processor.response.GenResponse;
import com.$PACK$.codegen.processor.service.GenService;
import com.$PACK$.codegen.processor.service.GenServiceImpl;
import com.$PACK$.codegen.processor.vo.GenVo;
import com.$PACK$.jpa.BaseJpaAggregate;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author $author$ 2023/4/10
 */
@GenRequest(pkgName = "$PACKAGE$.request")
@GenResponse(pkgName = "$PACKAGE$.response")
@GenDto(pkgName = "$PACKAGE$.request")
@GenVo(pkgName = "$PACKAGE$.response")
@GenController(pkgName = "$CONTROLLER$.controller")
@GenService(pkgName = "$PACKAGE$.service")
@GenServiceImpl(pkgName = "$PACKAGE$.service.impl")
@GenRepository(pkgName = "$PACKAGE$.repository")
@GenMapper(pkgName = "$PACKAGE$.mapper")
@Entity
@Table(name = "$TABLE_NAME$")
@Data
public class $ENTITY$ extends BaseJpaAggregate {

    @Convert(converter = ValidStatusConverter.class)
    @FieldDesc("状态")
    private ValidStatus status;
    
    public void init(){
        setStatus(ValidStatus.VALID);
    }

    public void valid(){
        setStatus(ValidStatus.VALID);
    }

    public void invalid(){
        setStatus(ValidStatus.INVALID);
    }
}
```
### 3. 编译

```shell
mvn clean compile
```
