package com.hp.codegen.processor.domain;


import com.google.auto.service.AutoService;
import com.hp.codegen.annotation.domain.GenFindByPageQuery;
import com.hp.codegen.annotation.domain.GenQueryRepository;
import com.hp.codegen.context.CodeGenContextHolder;
import com.hp.codegen.spi.CodeGenProcessor;
import com.hp.common.base.model.PageRequestWrapper;
import com.hp.common.base.model.PageResponse;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author hp
 */
@AutoService(value = CodeGenProcessor.class)
public class GenQueryRepositoryProcessor extends AbstractDomainCodeGenProcessor {

    @Override
    protected boolean isInterface() {
        return true;
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenQueryRepository.class;
    }

    @Override
    protected void customizeType(TypeElement typeElement, RoundEnvironment roundEnvironment, TypeSpec.Builder typeSpecBuilder) {
        typeSpecBuilder
                .addModifiers(Modifier.PUBLIC);

        createFindByIdMethod().ifPresent(typeSpecBuilder::addMethod);
        createFindAllByIdMethod().ifPresent(typeSpecBuilder::addMethod);
        createFindByPageMethod().ifPresent(typeSpecBuilder::addMethod);
    }

    protected Optional<MethodSpec> createFindByIdMethod() {
        final MethodSpec methodSpec = MethodSpec.methodBuilder("find%sById".formatted(CodeGenContextHolder.getCurrentTypeName()))
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(Long.class, "id")
                .returns(ParameterizedTypeName.get(ClassName.get(Optional.class), CodeGenContextHolder.getCurrentTypeClassName()))
                .build();
        return Optional.of(methodSpec);
    }

    protected Optional<MethodSpec> createFindAllByIdMethod() {
        final MethodSpec methodSpec = MethodSpec.methodBuilder("findAll%sById".formatted(CodeGenContextHolder.getCurrentTypeName()))
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(ParameterizedTypeName.get(ClassName.get(Collection.class), ClassName.get(Long.class)), "ids")
                .returns(ParameterizedTypeName.get(ClassName.get(List.class), CodeGenContextHolder.getCurrentTypeClassName()))
                .build();
        return Optional.of(methodSpec);
    }

    protected Optional<MethodSpec> createFindByPageMethod() {
        final MethodSpec methodSpec = MethodSpec.methodBuilder("find%sByPage".formatted(CodeGenContextHolder.getCurrentTypeName()))
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(
                        ParameterizedTypeName.get(
                                ClassName.get(PageRequestWrapper.class),
                                CodeGenContextHolder.getClassName(GenFindByPageQuery.class)),
                        "queryWrapper"
                )
                .returns(
                        ParameterizedTypeName.get(
                                ClassName.get(PageResponse.class),
                                CodeGenContextHolder.getCurrentTypeClassName()
                        )
                )
                .build();

        return Optional.of(methodSpec);
    }
}
