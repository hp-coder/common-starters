package com.luban.codegen.processor.service;

import java.lang.annotation.*;

/**
 * @author gim
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GenService {
  String pkgName();
  String sourcePath() default "src/main/java";
  boolean overrideSource() default false;
}
