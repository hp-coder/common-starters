package com.luban.codegen.processor.request;

import com.google.auto.service.AutoService;
import com.luban.jpa.AbstractBaseJpaDTO;
import com.luban.codegen.processor.AbstractCodeGenProcessor;
import com.luban.codegen.processor.response.GenResponse;
import com.luban.codegen.processor.vo.Ignore;
import com.luban.codegen.spi.CodeGenProcessor;
import com.squareup.javapoet.TypeSpec;
import lombok.Data;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Set;

/**
 * @author HP
 * @date 2022/10/24
 */
@AutoService(CodeGenProcessor.class)
public class GenRequestProcessor extends AbstractCodeGenProcessor {

    public static final String SUFFIX = "Request";

    @Override
    protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        Set<VariableElement> fields = findFields(typeElement, v -> Objects.isNull(v.getAnnotation(Ignore.class)));
        String sourceClassName = typeElement.getSimpleName() + SUFFIX;
        TypeSpec.Builder builder = TypeSpec.classBuilder(sourceClassName)
                .superclass(AbstractBaseJpaDTO.class)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Data.class);
        generateGettersAndSettersWithLombok(builder, fields);
        String packageName = generatePackage(typeElement);
        generateJavaSourceFile(packageName, typeElement.getAnnotation(GenResponse.class).sourcePath(), builder);
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenRequest.class;
    }

    @Override
    public String generatePackage(TypeElement typeElement) {
        return typeElement.getAnnotation(GenRequest.class).pkgName();
    }
}
