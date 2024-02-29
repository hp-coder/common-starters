package com.hp.codegen.processor.request;

import com.google.auto.service.AutoService;
import com.hp.codegen.constant.Orm;
import com.hp.codegen.context.ProcessingEnvironmentContextHolder;
import com.hp.codegen.processor.AbstractCodeGenProcessor;
import com.hp.codegen.processor.Ignore;
import com.hp.codegen.spi.CodeGenProcessor;
import com.hp.common.base.model.Request;
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
public class GenCreateRequestProcessor extends AbstractCodeGenProcessor {

    public static final String SUFFIX = "CreateRequest";

    @Override
    public boolean supportedOrm(Orm orm) {
        return true;
    }

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
                .addSuperinterface(Request.class)
                .addModifiers(Modifier.PUBLIC);


        generateGettersAndSettersWithLombok(builder, fields, ProcessingEnvironmentContextHolder.getFieldSpecModifiers());
        generateJavaSourceFile(generatePackage(typeElement), generatePath(typeElement), builder);
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenCreateRequest.class;
    }

    @Override
    public String generatePackage(TypeElement typeElement) {
        return typeElement.getAnnotation(GenCreateRequest.class).pkgName();
    }

    @Override
    public String generatePath(TypeElement typeElement) {
        return typeElement.getAnnotation(GenCreateRequest.class).sourcePath();
    }
}
