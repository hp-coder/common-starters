package com.luban.codegen.processor.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.google.auto.service.AutoService;
import com.luban.codegen.context.ProcessingEnvironmentContextHolder;
import com.luban.codegen.processor.AbstractCodeGenProcessor;
import com.luban.codegen.processor.Ignore;
import com.luban.codegen.spi.CodeGenProcessor;
import com.luban.common.base.model.PageRequest;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;

/**
 * @author hp
 * @date 2022/10/24
 */
@AutoService(CodeGenProcessor.class)
public class GenPageRequestProcessor extends AbstractCodeGenProcessor {

    public static final String SUFFIX = "PageRequest";

    public static String getPageRequestName(TypeElement typeElement) {
        return typeElement.getSimpleName() + SUFFIX;
    }

    @Override
    protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        final List<VariableElement> fields = findFields(typeElement, v ->
                Objects.isNull(v.getAnnotation(Ignore.class)) &&
                        Objects.isNull(v.getAnnotation(Deprecated.class))
        );
        final TypeSpec.Builder builder = TypeSpec.classBuilder(getPageRequestName(typeElement))
                .addSuperinterface(PageRequest.class)
                .addModifiers(Modifier.PUBLIC);

        builder.addField(
                FieldSpec.builder(Integer.class, "page", Modifier.PRIVATE)
                        .addAnnotation(
                                AnnotationSpec.builder(JsonAlias.class)
                                        .addMember("value", "\"page\"")
                                        .build()
                        )
                        .build()
        );
        builder.addField(
                FieldSpec.builder(Integer.class, "size", Modifier.PRIVATE)
                        .addAnnotation(
                                AnnotationSpec.builder(JsonAlias.class)
                                        .addMember("value", "\"size\"")
                                        .build()
                        )
                        .build()
        );

        generateGettersAndSettersWithLombok(builder, fields, ProcessingEnvironmentContextHolder.getFieldSpecModifiers());
        generateJavaSourceFile(generatePackage(typeElement), generatePath(typeElement), builder);
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenPageRequest.class;
    }

    @Override
    public String generatePackage(TypeElement typeElement) {
        return typeElement.getAnnotation(GenPageRequest.class).pkgName();
    }

    @Override
    public String generatePath(TypeElement typeElement) {
        return typeElement.getAnnotation(GenPageRequest.class).sourcePath();
    }
}
