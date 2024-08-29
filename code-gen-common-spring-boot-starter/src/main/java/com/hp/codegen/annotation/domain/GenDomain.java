package com.hp.codegen.annotation.domain;

import java.lang.annotation.*;

/**
 * @author hp
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@GenCommandRepository(packageName = "", subPackageName = "facade.repository")
@GenCreateCommand(packageName = "", subPackageName = "command")
@GenUpdateCommand(packageName = "", subPackageName = "command")
@GenCreateContext(packageName = "", subPackageName = "context")
@GenUpdateContext(packageName = "", subPackageName = "context")
@GenEvent(packageName = "", subPackageName = "event")
@GenFindByIdQuery(packageName = "", subPackageName = "query")
@GenFindAllByIdQuery(packageName = "", subPackageName = "query")
@GenFindByPageQuery(packageName = "", subPackageName = "query")
@GenMapper(packageName = "", subPackageName = "facade.mapper")
@GenCustomMapper(packageName = "", subPackageName = "facade.mapper")
@GenQueryRepository(packageName = "", subPackageName = "facade.repository")
public @interface GenDomain {

    String packageName();

    String sourcePath() default "src/main/java";
}
