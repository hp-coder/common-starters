package com.hp.codegen.processor.app;


import com.google.auto.service.AutoService;
import com.hp.codegen.annotation.app.GenQueryAppService;
import com.hp.codegen.annotation.domain.GenFindAllByIdQuery;
import com.hp.codegen.annotation.domain.GenFindByIdQuery;
import com.hp.codegen.annotation.domain.GenFindByPageQuery;
import com.hp.codegen.annotation.model.GenPageRequest;
import com.hp.codegen.annotation.model.GenPageResponse;
import com.hp.codegen.annotation.model.GenResponse;
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
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;

/**
 * @author hp
 */
@AutoService(value = CodeGenProcessor.class)
public class GenQueryAppServiceProcessor extends AbstractAppCodeGenProcessor {

    @Override
    protected boolean isInterface() {
        return true;
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenQueryAppService.class;
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
        final String methodName = "find%sById".formatted(CodeGenContextHolder.getCurrentTypeName());
        if (!CodeGenContextHolder.isAnnotationPresent(GenResponse.class)) {
            CodeGenContextHolder.log(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    String.format("To generate %s() on %s requires @GenResponse.", methodName, this.currentGeneratingTypeName)
            );
            return Optional.empty();
        }

        final MethodSpec methodSpec = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(CodeGenContextHolder.getClassName(GenFindByIdQuery.class),"query")
                .returns(CodeGenContextHolder.getClassName(GenResponse.class))
                .build();

        return Optional.of(methodSpec);
    }

    protected Optional<MethodSpec> createFindAllByIdMethod() {
        final String methodName = "findAll%sById".formatted(CodeGenContextHolder.getCurrentTypeName());
        if (!CodeGenContextHolder.isAnnotationPresent(GenResponse.class)) {
            CodeGenContextHolder.log(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    String.format("To generate %s() on %s requires @GenResponse.", methodName, this.currentGeneratingTypeName)
            );
            return Optional.empty();
        }

        final MethodSpec methodSpec = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(CodeGenContextHolder.getClassName(GenFindAllByIdQuery.class), "query")
                .returns(ParameterizedTypeName.get(ClassName.get(List.class), CodeGenContextHolder.getClassName(GenResponse.class)))
                .build();

        return Optional.of(methodSpec);
    }

    protected Optional<MethodSpec> createFindByPageMethod() {
        final String methodName = "find%sByPage".formatted(CodeGenContextHolder.getCurrentTypeName());
        if (CodeGenContextHolder.missingAnyAnnotated(
                GenPageRequest.class,
                GenPageResponse.class
        )) {
            CodeGenContextHolder.log(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    String.format("To generate %s() on %s requires @GenPageRequest and @GenPageResponse.", methodName, this.currentGeneratingTypeName)
            );
            return Optional.empty();
        }
        final MethodSpec methodSpec = MethodSpec.methodBuilder(methodName)
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
                                CodeGenContextHolder.getClassName(GenPageResponse.class)
                        )
                )
                .build();

        return Optional.of(methodSpec);
    }
}
