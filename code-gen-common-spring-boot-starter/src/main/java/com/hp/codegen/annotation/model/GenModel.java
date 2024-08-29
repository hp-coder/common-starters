package com.hp.codegen.annotation.model;

import java.lang.annotation.*;

/**
 * @author hp
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@GenRequest(packageName = "", subPackageName = "request")
@GenCreateRequest(packageName = "", subPackageName = "request")
@GenPageRequest(packageName = "", subPackageName = "request")
@GenUpdateRequest(packageName = "", subPackageName = "request")
@GenResponse(packageName = "", subPackageName = "response")
@GenPageResponse(packageName = "", subPackageName = "response")
@GenDeserializer(packageName = "", subPackageName = "deserializer")
@GenSerializer(packageName = "", subPackageName = "serializer")
public @interface GenModel {

    String packageName();

    String sourcePath() default "src/main/java";
}
