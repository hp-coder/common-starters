package com.luban.codegen.processor.request;

import com.google.auto.service.AutoService;
import com.google.common.collect.Lists;
import com.luban.codegen.constant.Orm;
import com.luban.codegen.processor.AbstractCodeGenProcessor;
import com.luban.codegen.processor.Ignore;
import com.luban.codegen.processor.modifier.BaseEnumFieldSpecModifier;
import com.luban.codegen.processor.modifier.DefaultToStringFieldSpecModifier;
import com.luban.codegen.processor.modifier.FieldSpecModifier;
import com.luban.codegen.processor.modifier.jpa.JpaConverterFieldSpecModifier;
import com.luban.codegen.spi.CodeGenProcessor;
import com.luban.common.base.model.Request;
import com.luban.jpa.BaseJpaAggregate;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author hp
 * @date 2022/10/24
 */
@AutoService(CodeGenProcessor.class)
public class GenRequestProcessor extends AbstractCodeGenProcessor {

    public static final String SUFFIX = "Request";

    @Override
    protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        final List<VariableElement> fields = findFields(typeElement, v ->
                Objects.isNull(v.getAnnotation(Ignore.class)) && Objects.isNull(v.getAnnotation(Deprecated.class))
        );
        final String sourceClassName = typeElement.getSimpleName() + SUFFIX;
        final TypeSpec.Builder builder = TypeSpec.classBuilder(sourceClassName)
                .addSuperinterface(Request.class)
                .addModifiers(Modifier.PUBLIC);

        getSuperClass(typeElement).ifPresent(superclass -> {
            if (superclass.getQualifiedName().contentEquals(BaseJpaAggregate.class.getCanonicalName())) {
                builder.addField(FieldSpec.builder(Long.class, "id", Modifier.PRIVATE).build());
            }
        });

        final ArrayList<FieldSpecModifier> fieldSpecModifiers = Lists.newArrayList(
                new DefaultToStringFieldSpecModifier(),
                new JpaConverterFieldSpecModifier(),
                new BaseEnumFieldSpecModifier()
        );
        generateGettersAndSettersWithLombok(builder, fields, fieldSpecModifiers);
        generateJavaSourceFile(generatePackage(typeElement), generatePath(typeElement), builder);
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenRequest.class;
    }

    @Override
    public String generatePackage(TypeElement typeElement) {
        return typeElement.getAnnotation(GenRequest.class).pkgName();
    }

    @Override
    public String generatePath(TypeElement typeElement) {
        return typeElement.getAnnotation(GenRequest.class).sourcePath();
    }
}
