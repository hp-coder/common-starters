package com.hp.codegen.processor.domain;


import com.google.auto.service.AutoService;
import com.hp.codegen.annotation.domain.GenCommandRepository;
import com.hp.codegen.context.CodeGenContextHolder;
import com.hp.codegen.spi.CodeGenProcessor;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.Optional;

/**
 * @author hp
 */
@AutoService(value = CodeGenProcessor.class)
public class GenCommandRepositoryProcessor extends AbstractDomainCodeGenProcessor {

    @Override
    protected boolean isInterface() {
        return true;
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenCommandRepository.class;
    }

    @Override
    protected void customizeType(TypeElement typeElement, RoundEnvironment roundEnvironment, TypeSpec.Builder typeSpecBuilder) {
        typeSpecBuilder
                .addModifiers(Modifier.PUBLIC);

        createSaveMethod().ifPresent(typeSpecBuilder::addMethod);
    }

    protected Optional<MethodSpec> createSaveMethod() {
        final String currentTypeName = CodeGenContextHolder.getCurrentTypeName();
        final MethodSpec methodSpec = MethodSpec.methodBuilder("save%s".formatted(currentTypeName))
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(
                        ParameterSpec.builder(CodeGenContextHolder.getCurrentTypeClassName(), CodeGenContextHolder.getCurrentTypeFieldName())
                                .build()
                )
                .build();
        return Optional.of(methodSpec);
    }
}
