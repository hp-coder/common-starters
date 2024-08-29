package com.hp.codegen.processor.app;

import com.google.auto.service.AutoService;
import com.hp.codegen.annotation.app.GenEventListener;
import com.hp.codegen.annotation.domain.*;
import com.hp.codegen.processor.domain.GenEventProcessor;
import com.hp.codegen.annotation.domain.*;
import com.hp.codegen.context.CodeGenContextHolder;
import com.hp.codegen.spi.CodeGenProcessor;
import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.Optional;

/**
 * @author hp
 */

@AutoService(CodeGenProcessor.class)
public class GenEventListenerProcessor extends AbstractAppCodeGenProcessor {

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenEventListener.class;
    }

    @Override
    protected boolean creatable(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        if (CodeGenContextHolder.missingAnyAnnotated(GenEvent.class)) {
            CodeGenContextHolder.log(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    String.format("To generate %s requires @GenEvent.", this.currentGeneratingTypeName)
            );
            return false;
        }
        return true;
    }

    @Override
    protected void customizeType(TypeElement typeElement, RoundEnvironment roundEnvironment, TypeSpec.Builder typeSpecBuilder) {
        typeSpecBuilder
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Slf4j.class)
                .addAnnotation(Component.class)
                .addAnnotation(RequiredArgsConstructor.class)
                .addAnnotation(
                        AnnotationSpec.builder(Transactional.class)
                                .addMember("rollbackFor", "$L", "Exception.class")
                                .build()
                )
                .addField(
                        FieldSpec.builder(CodeGenContextHolder.getClassName(GenCommandRepository.class), CodeGenContextHolder.getClassFieldName(GenCommandRepository.class), Modifier.PRIVATE, Modifier.FINAL)
                                .build()
                );

        createCreatedHandlerMethod().ifPresent(typeSpecBuilder::addMethod);
        createUpdatedHandlerMethod().ifPresent(typeSpecBuilder::addMethod);
    }

    protected Optional<MethodSpec> createCreatedHandlerMethod() {
        final String currentTypeName = CodeGenContextHolder.getCurrentTypeName();
        final String methodName = String.format("handle%sCreated", currentTypeName);
        if (CodeGenContextHolder.missingAnyAnnotated(
                GenEvent.class,
                GenCreateContext.class,
                GenCreateCommand.class
        )) {
            CodeGenContextHolder.log(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    String.format("To generate %s for %s requires @GenEvent @GenCreateContext and @GenCreateCommand.", methodName, this.currentGeneratingTypeName)
            );
            return Optional.empty();
        }

        final MethodSpec methodSpec = MethodSpec.methodBuilder(methodName)
                .addAnnotation(EventListener.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(
                        ParameterSpec.builder(
                                        ClassName.get(
                                                CodeGenContextHolder.getPackageName(GenEvent.class),
                                                CodeGenContextHolder.getClassSimpleName(GenEvent.class),
                                                GenEventProcessor.getCreatedEventName(CodeGenContextHolder.getCurrentTypeElement())
                                        ),
                                        "event"
                                )
                                .build()
                )
                .addCode(
                        CodeBlock.of(
                                """
                                        final $T entity = event.get$L();
                                        // handle event
                                        """,
                                ClassName.get(CodeGenContextHolder.getCurrentTypeElement()),
                                currentTypeName
                        )
                )
                .build();
        return Optional.of(methodSpec);
    }

    protected Optional<MethodSpec> createUpdatedHandlerMethod() {
        final String currentTypeName = CodeGenContextHolder.getCurrentTypeName();
        final String methodName = String.format("handle%sUpdated", currentTypeName);
        if (CodeGenContextHolder.missingAnyAnnotated(
                GenEvent.class,
                GenUpdateContext.class,
                GenUpdateCommand.class
        )) {
            CodeGenContextHolder.log(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    String.format("To generate %s for %s requires @GenEvent @GenUpdateContext and @GenUpdateCommand.", methodName, this.currentGeneratingTypeName)
            );
            return Optional.empty();
        }

        return Optional.of(
                MethodSpec.methodBuilder(methodName)
                        .addAnnotation(EventListener.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(
                                ParameterSpec.builder(
                                                ClassName.get(
                                                        CodeGenContextHolder.getPackageName(GenEvent.class),
                                                        CodeGenContextHolder.getClassSimpleName(GenEvent.class),
                                                        GenEventProcessor.getUpdatedEventName(CodeGenContextHolder.getCurrentTypeElement())
                                                ),
                                                "event"
                                        )
                                        .build()
                        )
                        .addCode(
                                CodeBlock.of(
                                        """
                                                final $T entity = event.get$L();
                                                // handle event
                                                """,
                                        ClassName.get(CodeGenContextHolder.getCurrentTypeElement()),
                                        currentTypeName
                                )
                        )
                        .build()
        );
    }
}
