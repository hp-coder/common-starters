# 自定义代码生成器

## 介绍

- 使用 SPI 机制 + Google AutoService获取实现类
- 使用 JavaPoet 编排具体类的内容
- 对于JPA架构下的项目, Mapper将作为MapStruct功能的实现,与Mybatis的Mapper概念存在冲突, 为了兼容处理, 对于mybatis中的Mapper概念将在repository包中处理
- 对于后期不需根据业务修改内容的类可以直接生成到target下
- 支持框架
  - Spring Data Jpa
  - Mybatis-Plus

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
<project>
    <!--...省略其他节点-->
    
    <parent>
        <!--其中包含了配置的apt插件, 和主要版本-->
        <artifactId>common-starters</artifactId>
        <groupId>com.luban</groupId>
        <version>1.0.0-sp3.2-SNAPSHOT</version>
    </parent>
    
    <properties>
        <!--1.0.0-sp3.2-SNAPSHOT不支持生成Mybatis-Plus的代码-->
        <code-gen-version>1.0.0-sp3.2-SNAPSHOT</code-gen-version>
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
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <dependency>
            <groupId>com.luban</groupId>
            <artifactId>code-gen-common-spring-boot-starter</artifactId>
            <version>${code-gen-version}</version>
        </dependency>
        <!--代码生成器从1.0.1-sp2-SNAPSHOT开始将以下两个依赖改为 option=true -->
        <!--所以需要客户端指定引入具体starter-->
        <!--======================== separate line =============================-->
        <!--默认-->
        <!--代码生成器生成符合JPA的代码-->
        <dependency>
            <groupId>com.luban</groupId>
            <artifactId>jpa-common-spring-boot-starter</artifactId>
        </dependency>
        <!--======================== separate line =============================-->
        <!--在编译时添加自定义参数 -Aorm=mbp 可以生成符合Mybatis-Plus的代码-->
        <dependency>
            <groupId>com.luban</groupId>
            <artifactId>mybatis-plus-common-spring-boot-starter</artifactId>
        </dependency>
    </dependencies>
        
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>${maven-compiler-plugin.version}</version>
                <configuration>
                    <source>${maven.compiler.source}</source>
                    <target>${maven.compiler.target}</target>
                    <encoding>${project.build.sourceEncoding}</encoding>
                    <!--需生成Mbp代码时必须使用该参数-->
                    <compilerArgs>
                        <arg>-Aorm=mbp</arg>
                    </compilerArgs>
                    <compilerArgument>-Xlint:unchecked</compilerArgument>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>com.luban</groupId>
                            <artifactId>code-gen-common-spring-boot-starter</artifactId>
                            <version>${code-gen-starter.version}</version>
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
            <!--该插件由 common-bom 定义, 主要用于支持JDK11环境下生成QueryDSL的Q类-->
            <!--JDK17环境下, 该插件可不引入-->
            <!--生成Mbp代码时无需引入-->
            <plugin>
                <groupId>com.mysema.maven</groupId>
                <artifactId>apt-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### 2. Entity模版

> idea - settings - live template


#### JPA模版1

适用非Jakarta环境
```shell
import com.luban.codegen.processor.controller.GenController;
import com.luban.codegen.processor.dto.GenDto;
import com.luban.codegen.processor.mapper.GenMapper;
import com.luban.codegen.processor.repository.GenRepository;
import com.luban.codegen.processor.request.GenRequest;
import com.luban.codegen.processor.response.GenResponse;
import com.luban.codegen.processor.service.GenService;
import com.luban.codegen.processor.service.GenServiceImpl;
import com.luban.codegen.processor.vo.GenVo;
import com.luban.common.base.annotations.FieldDesc;
import com.luban.common.base.enums.ValidStatus;
import com.luban.jpa.BaseJpaAggregate;
import com.luban.jpa.convertor.LocalDateTimeConverter;
import com.luban.jpa.convertor.ValidStatusConverter;
import lombok.Data;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * @author hp 2023/4/10
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
#### JPA2

使用于Jakarta环境
```java

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * @author hp 2023/4/10
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

#### Mbp模版
```java
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.luban.codegen.processor.controller.GenController;
import com.luban.codegen.processor.dto.GenDto;
import com.luban.codegen.processor.mapper.GenMapper;
import com.luban.codegen.processor.repository.GenRepository;
import com.luban.codegen.processor.request.GenRequest;
import com.luban.codegen.processor.response.GenResponse;
import com.luban.codegen.processor.service.GenService;
import com.luban.codegen.processor.service.GenServiceImpl;
import com.luban.codegen.processor.vo.GenVo;
import com.luban.common.base.annotations.FieldDesc;
import com.luban.common.base.enums.ValidStatus;
import com.luban.mybatisplus.BaseMbpAggregate;
import com.luban.mybatisplus.convertor.ValidStatusConverter;
import lombok.Data;

/**
 * @author hp 2023/4/10
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
@TableName(value="$TABLE_NAME$")
@Data
public class $ENTITY$ extends BaseMbpAggregate {

    @TableField(typeHandler = ValidStatusConverter.class)
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
mvn clean compile [-Aorm=mbp|-Aorm=jpa]
```
