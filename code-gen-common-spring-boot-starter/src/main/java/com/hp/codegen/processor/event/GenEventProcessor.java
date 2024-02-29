package com.hp.codegen.processor.event;

import com.google.auto.service.AutoService;
import com.hp.codegen.processor.AbstractCodeGenProcessor;
import com.hp.codegen.spi.CodeGenProcessor;
import com.hp.codegen.util.StringUtils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;
import lombok.Value;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.Optional;


/**
 * @author hp
 * @date 2022/10/24
 */
@AutoService(CodeGenProcessor.class)
public class GenEventProcessor extends AbstractCodeGenProcessor {

    public static final String SUFFIX = "Events";
    public static final String CREATED_SUFFIX = "CreatedEvent";
    public static final String UPDATED_SUFFIX = "UpdatedEvent";

    public static String getCreatedEventName(TypeElement typeElement) {
        return typeElement.getSimpleName() + CREATED_SUFFIX;
    }

    public static String getUpdatedEventName(TypeElement typeElement) {
        return typeElement.getSimpleName() + UPDATED_SUFFIX;
    }

    @Override
    protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        final TypeSpec.Builder builder =
                TypeSpec.interfaceBuilder(typeElement.getSimpleName() + SUFFIX)
                        .addModifiers(Modifier.PUBLIC);

        createdEventClass(typeElement).ifPresent(builder::addType);
        updatedEventClass(typeElement).ifPresent(builder::addType);

        generateJavaSourceFile(generatePackage(typeElement), generatePath(typeElement), builder);
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenEvent.class;
    }

    @Override
    public String generatePackage(TypeElement typeElement) {
        return typeElement.getAnnotation(GenEvent.class).pkgName();
    }

    @Override
    public String generatePath(TypeElement typeElement) {
        return typeElement.getAnnotation(GenEvent.class).sourcePath();
    }

    private Optional<TypeSpec> createdEventClass(TypeElement typeElement) {
        if (StringUtils.containsNull(getNameContext().getCreateContextPackageName(), getNameContext().getCreateContextClassName())) {
            return Optional.empty();
        }
        return Optional.of(
                TypeSpec.classBuilder(getCreatedEventName(typeElement))
                        .addAnnotation(Value.class)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addField(
                                FieldSpec.builder(ClassName.get(getNameContext().getCreateContextPackageName(), getNameContext().getCreateContextClassName()), "context")
                                        .build()
                        )
                        .build()
        );
    }

    private Optional<TypeSpec> updatedEventClass(TypeElement typeElement) {
        if (StringUtils.containsNull(getNameContext().getUpdateContextPackageName(), getNameContext().getUpdateContextClassName())) {
            return Optional.empty();
        }
        return Optional.of(
                TypeSpec.classBuilder(getUpdatedEventName(typeElement))
                        .addAnnotation(Value.class)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addField(
                                FieldSpec.builder(ClassName.get(getNameContext().getUpdateContextPackageName(), getNameContext().getUpdateContextClassName()), "context")
                                        .build()
                        )
                        .build()
        );
    }
}
