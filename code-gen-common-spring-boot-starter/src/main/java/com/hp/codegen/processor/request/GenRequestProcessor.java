package com.hp.codegen.processor.request;

import com.google.auto.service.AutoService;
import com.google.common.collect.Lists;
import com.hp.codegen.context.ProcessingEnvironmentContextHolder;
import com.hp.codegen.processor.AbstractCodeGenProcessor;
import com.hp.codegen.processor.Ignore;
import com.hp.codegen.processor.modifier.BaseEnumFieldSpecModifier;
import com.hp.codegen.processor.modifier.DefaultToStringFieldSpecModifier;
import com.hp.codegen.processor.modifier.FieldSpecModifier;
import com.hp.codegen.processor.modifier.jpa.JpaConverterFieldSpecModifier;
import com.hp.codegen.processor.modifier.mybatisplus.MybatisplusTypeHandlerFieldSpecModifier;
import com.hp.codegen.spi.CodeGenProcessor;
import com.hp.common.base.model.Request;
import com.hp.jpa.BaseJpaAggregate;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
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
                new MybatisplusTypeHandlerFieldSpecModifier(),
                new BaseEnumFieldSpecModifier()
        );
        generateGettersAndSettersWithLombok(builder, fields, ProcessingEnvironmentContextHolder.getFieldSpecModifiers());
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
