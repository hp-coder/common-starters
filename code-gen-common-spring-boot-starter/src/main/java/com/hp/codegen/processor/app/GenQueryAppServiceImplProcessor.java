package com.hp.codegen.processor.app;


import com.google.auto.service.AutoService;
import com.google.common.collect.Lists;
import com.hp.codegen.annotation.app.GenQueryAppService;
import com.hp.codegen.annotation.app.GenQueryAppServiceImpl;
import com.hp.codegen.annotation.domain.*;
import com.hp.codegen.annotation.model.GenPageResponse;
import com.hp.codegen.annotation.model.GenResponse;
import com.hp.codegen.annotation.domain.*;
import com.hp.codegen.context.CodeGenContextHolder;
import com.hp.codegen.spi.CodeGenProcessor;
import com.hp.codegen.util.CodeGenHelper;
import com.hp.common.base.enums.CodeEnum;
import com.hp.common.base.exception.BusinessException;
import com.hp.common.base.model.PageRequestWrapper;
import com.hp.common.base.model.PageResponse;
import com.squareup.javapoet.*;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
public class GenQueryAppServiceImplProcessor extends AbstractAppCodeGenProcessor {

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenQueryAppServiceImpl.class;
    }

    @Override
    protected boolean creatable(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        if (!CodeGenContextHolder.isAnnotationPresent(GenQueryAppService.class)) {
            CodeGenContextHolder.log(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    String.format("To generate %s requires @GenQueryAppService.", this.currentGeneratingTypeName)
            );
            return false;
        }
        return true;
    }

    @Override
    protected void customizeType(TypeElement typeElement, RoundEnvironment roundEnvironment, TypeSpec.Builder typeSpecBuilder) {
        typeSpecBuilder
                .addSuperinterface(CodeGenContextHolder.getClassName(GenQueryAppService.class))
                .addAnnotation(Slf4j.class)
                .addAnnotation(Service.class)
                .addAnnotation(RequiredArgsConstructor.class)
                .addModifiers(Modifier.PUBLIC)
                .addFields(createInjectionFields());

        createFindByIdMethod().ifPresent(typeSpecBuilder::addMethod);
        createFindAllByIdMethod().ifPresent(typeSpecBuilder::addMethod);
        createFindByPageMethod().ifPresent(typeSpecBuilder::addMethod);

    }

    @Nonnull
    protected static List<FieldSpec> createInjectionFields() {
        final List<FieldSpec> fieldSpecs = Lists.newArrayList();

        if (CodeGenContextHolder.isAnnotationPresent(GenQueryRepository.class)) {
            final FieldSpec repositoryInjectionField = CodeGenHelper.createFieldSpecBuilder(GenQueryRepository.class)
                    .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                    .build();
            fieldSpecs.add(repositoryInjectionField);
        }

        return fieldSpecs;
    }

    protected Optional<MethodSpec> createFindByIdMethod() {
        final String methodName = "find%sById".formatted(CodeGenContextHolder.getCurrentTypeName());
        if (CodeGenContextHolder.missingAnyAnnotated(
                GenResponse.class,
                GenMapper.class,
                GenQueryRepository.class
        )) {
            CodeGenContextHolder.log(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    String.format("To generate %s() on %s requires @GenResponse @GenMapper and @GenQueryRepository.", methodName, this.currentGeneratingTypeName)
            );
            return Optional.empty();
        }

        final MethodSpec methodSpec = MethodSpec.methodBuilder(methodName)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(CodeGenContextHolder.getClassName(GenFindByIdQuery.class), "query")
                .addCode(
                        CodeBlock.of(
                                """
                                        return $L.find$LById(query.getId())
                                        .map($T.INSTANCE::entityToCustomResponse)
                                        .orElseThrow(() -> new $T($T.NotFindError));
                                        """,
                                CodeGenContextHolder.getClassFieldName(GenQueryRepository.class),
                                CodeGenContextHolder.getCurrentTypeName(),
                                CodeGenContextHolder.getClassName(GenMapper.class),
                                ClassName.get(BusinessException.class),
                                ClassName.get(CodeEnum.class)
                        )
                )
                .returns(CodeGenContextHolder.getClassName(GenResponse.class))
                .build();

        return Optional.of(methodSpec);
    }

    protected Optional<MethodSpec> createFindAllByIdMethod() {
        final String methodName = "findAll%sById".formatted(CodeGenContextHolder.getCurrentTypeName());
        if (CodeGenContextHolder.missingAnyAnnotated(
                GenResponse.class,
                GenMapper.class,
                GenQueryRepository.class
        )) {
            CodeGenContextHolder.log(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    String.format("To generate %s() on %s requires @GenResponse @GenMapper and @GenQueryRepository.", methodName, this.currentGeneratingTypeName)
            );
            return Optional.empty();
        }

        final MethodSpec methodSpec = MethodSpec.methodBuilder(methodName)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(CodeGenContextHolder.getClassName(GenFindAllByIdQuery.class), "query")
                .addCode(
                        CodeBlock.of(
                                """
                                        return $L.findAll$LById(query.getIds())
                                                .stream()
                                                .map($T.INSTANCE::entityToCustomResponse)
                                                .toList();
                                        """,
                                CodeGenContextHolder.getClassFieldName(GenQueryRepository.class),
                                CodeGenContextHolder.getCurrentTypeName(),
                                CodeGenContextHolder.getClassName(GenMapper.class)
                        )
                )
                .returns(ParameterizedTypeName.get(ClassName.get(List.class), CodeGenContextHolder.getClassName(GenResponse.class)))
                .build();

        return Optional.of(methodSpec);
    }

    protected Optional<MethodSpec> createFindByPageMethod() {
        final String methodName = "find%sByPage".formatted(CodeGenContextHolder.getCurrentTypeName());
        if (CodeGenContextHolder.missingAnyAnnotated(
                GenFindByPageQuery.class,
                GenMapper.class,
                GenQueryRepository.class
        )) {
            CodeGenContextHolder.log(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    String.format("To generate %s() on %s requires @GenFindByPageQuery @GenMapper and @GenQueryRepository.", methodName, this.currentGeneratingTypeName)
            );
            return Optional.empty();
        }

        final MethodSpec methodSpec = MethodSpec.methodBuilder(methodName)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(
                        ParameterizedTypeName.get(
                                ClassName.get(PageRequestWrapper.class),
                                CodeGenContextHolder.getClassName(GenFindByPageQuery.class)
                        ),
                        "queryWrapper"
                )
                .addCode(
                        CodeBlock.of("final $T pageResponse = $L.find$LByPage(queryWrapper);\n",
                                ParameterizedTypeName.get(ClassName.get(PageResponse.class), CodeGenContextHolder.getCurrentTypeClassName()),
                                CodeGenContextHolder.getClassFieldName(GenQueryRepository.class),
                                CodeGenContextHolder.getCurrentTypeName()
                        )
                )
                .addCode(
                        CodeBlock.of(
                                """
                                           final $T records = pageResponse.getList()
                                                        .stream()
                                                        .map($T.INSTANCE::entityToCustomPageResponse)
                                                        .toList();
                                        """,
                                ParameterizedTypeName.get(ClassName.get(List.class), CodeGenContextHolder.getClassName(GenPageResponse.class)),
                                CodeGenContextHolder.getClassName(GenMapper.class)
                        )
                )
                .addCode(
                        CodeBlock.of(
                                """
                                           return $T.of(records, pageResponse);
                                        """,
                                ClassName.get(PageResponse.class)
                        )
                )
                .returns(ParameterizedTypeName.get(ClassName.get(PageResponse.class), CodeGenContextHolder.getClassName(GenPageResponse.class)))
                .build();

        return Optional.of(methodSpec);
    }

}
