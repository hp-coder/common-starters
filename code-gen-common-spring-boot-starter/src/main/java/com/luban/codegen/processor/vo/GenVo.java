package com.luban.codegen.processor.vo;

import com.luban.codegen.processor.Generate;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author hp
 * @date 2022/10/24
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Generate(
        pkgName = "",
        sourcePath = ""
)
public @interface GenVo {
    @AliasFor(value = "pkgName")
    String pkgName();

    @AliasFor(value = "sourcePath")
    String sourcePath() default "src/main/java";
}
