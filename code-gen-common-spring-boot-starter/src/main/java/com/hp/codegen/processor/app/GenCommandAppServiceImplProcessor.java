package com.hp.codegen.processor.app;


import com.google.auto.service.AutoService;
import com.google.common.collect.Lists;
import com.hp.codegen.annotation.app.GenCommandAppService;
import com.hp.codegen.annotation.app.GenCommandAppServiceImpl;
import com.hp.codegen.annotation.domain.GenCreateCommand;
import com.hp.codegen.annotation.domain.GenCreateContext;
import com.hp.codegen.annotation.domain.GenQueryRepository;
import com.hp.codegen.annotation.domain.GenCommandRepository;
import com.hp.codegen.context.CodeGenContextHolder;
import com.hp.codegen.spi.CodeGenProcessor;
import com.hp.codegen.util.CodeGenHelper;
import com.squareup.javapoet.*;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class GenCommandAppServiceImplProcessor extends AbstractAppCodeGenProcessor {

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenCommandAppServiceImpl.class;
    }

    @Override
    protected boolean creatable(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        if (!CodeGenContextHolder.isAnnotationPresent(GenCommandAppService.class)) {
            CodeGenContextHolder.log(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    String.format("To generate %s requires @GenService.", this.currentGeneratingTypeName)
            );
            return false;
        }
        return true;
    }

    @Override
    protected void customizeType(TypeElement typeElement, RoundEnvironment roundEnvironment, TypeSpec.Builder typeSpecBuilder) {
        typeSpecBuilder
                .addSuperinterface(CodeGenContextHolder.getClassName(GenCommandAppService.class))
                .addAnnotation(Slf4j.class)
                .addAnnotation(Service.class)
                .addAnnotation(RequiredArgsConstructor.class)
                .addAnnotation(AnnotationSpec.builder(Transactional.class).addMember("rollbackFor", "$L", "Exception.class").build())
                .addModifiers(Modifier.PUBLIC)
                .addFields(createInjectionFields());

        createCreateMethod().ifPresent(typeSpecBuilder::addMethod);
    }

    @Nonnull
    protected static List<FieldSpec> createInjectionFields() {
        final List<FieldSpec> fieldSpecs = Lists.newArrayList();

        if (CodeGenContextHolder.isAnnotationPresent(GenCommandRepository.class)) {
            final FieldSpec repositoryInjectionField = CodeGenHelper.createFieldSpecBuilder(GenCommandRepository.class)
                    .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                    .build();
            fieldSpecs.add(repositoryInjectionField);
        }

        if (CodeGenContextHolder.isAnnotationPresent(GenQueryRepository.class)) {
            final FieldSpec repositoryInjectionField = CodeGenHelper.createFieldSpecBuilder(GenQueryRepository.class)
                    .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                    .build();
            fieldSpecs.add(repositoryInjectionField);
        }

        return fieldSpecs;
    }

    protected Optional<MethodSpec> createCreateMethod() {
        final String methodName = "create" + CodeGenContextHolder.getCurrentTypeName();
        if (CodeGenContextHolder.missingAnyAnnotated(
                GenCreateCommand.class,
                GenCreateContext.class,
                GenCommandRepository.class
        )) {
            CodeGenContextHolder.log(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    String.format("To generate %s() on %s requires @GenCreateCommand @GenCreateContext and @GenCommandRepository.", methodName, this.currentGeneratingTypeName)
            );
            return Optional.empty();
        }

        final MethodSpec methodSpec = MethodSpec.methodBuilder(methodName)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(
                        CodeGenHelper.createParameterSpecBuilder(GenCreateCommand.class, "command")
                                .build()
                )
                .addCode(
                        CodeBlock.of(
                                "final $T context = $T.create(command);\n",
                                CodeGenContextHolder.getClassName(GenCreateContext.class),
                                CodeGenContextHolder.getClassName(GenCreateContext.class)
                        )
                )
                .addCode(
                        CodeBlock.of(
                                "final $T $L = $T.create(context);\n",
                                CodeGenContextHolder.getCurrentTypeElement(),
                                CodeGenContextHolder.getCurrentTypeFieldName(),
                                CodeGenContextHolder.getCurrentTypeElement()
                        )
                )
                .addCode(
                        CodeBlock.of(
                                "$L.save$L($L);\n",
                                CodeGenContextHolder.getClassFieldName(GenCommandRepository.class),
                                CodeGenContextHolder.getCurrentTypeName(),
                                CodeGenContextHolder.getCurrentTypeFieldName()
                        )
                )
                .build();

        return Optional.of(methodSpec);
    }

}
