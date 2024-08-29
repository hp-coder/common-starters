package com.hp.codegen.annotation.infrastructure;

import java.lang.annotation.*;

/**
 * @author hp
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@GenConverter(packageName = "", subPackageName = "converter")
@GenDao(packageName = "", subPackageName = "dao")
@GenPo(packageName = "", tablePrefix = "t_")
@GenMapperAdapter(packageName = "", subPackageName = "facade.mapper")
@GenQueryRepositoryAdapter(packageName = "", subPackageName = "facade.repository")
@GenCommandRepositoryAdapter(packageName = "", subPackageName = "facade.repository")
public @interface GenInfrastructure {
    String packageName();

    String sourcePath() default "src/main/java";

    String tablePrefix() default "t_";
}
