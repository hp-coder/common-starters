package com.hp.codegen.processor.domain;

import com.google.auto.service.AutoService;
import com.hp.codegen.annotation.domain.GenCreateCommand;
import com.hp.codegen.annotation.domain.GenCreateContext;
import com.hp.codegen.context.CodeGenContextHolder;
import com.hp.codegen.spi.CodeGenProcessor;
import com.hp.codegen.util.CodeGenHelper;
import com.hp.common.base.context.AbstractAggregationContext;
import com.hp.id.IdHelper;
import com.squareup.javapoet.*;
import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.Optional;

/**
 * @author hp
 */
@AutoService(value = CodeGenProcessor.class)
public class GenCreateContextProcessor extends AbstractDomainCodeGenProcessor {

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenCreateContext.class;
    }

    @Override
    protected boolean creatable(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        if (!CodeGenContextHolder.isAnnotationPresent(GenCreateCommand.class)) {
            CodeGenContextHolder.log(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    String.format("To generate %s requires @GenCreateCommand.", this.currentGeneratingTypeName)
            );
            return false;
        }
        return true;
    }

    @Override
    protected void customizeType(TypeElement typeElement, RoundEnvironment roundEnvironment, TypeSpec.Builder typeSpecBuilder) {
        typeSpecBuilder
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Getter.class)
                .superclass(
                        ParameterizedTypeName.get(
                                ClassName.get(AbstractAggregationContext.class),
                                ClassName.get(typeElement),
                                CodeGenContextHolder.getClassName(GenCreateCommand.class)
                        )
                )
                .addField(
                        FieldSpec.builder(Long.class, "createdBy", Modifier.PRIVATE)
                                .addAnnotation(Setter.class)
                                .build()
                );

        createConstructor().ifPresent(typeSpecBuilder::addMethod);
        createCreateMethod().ifPresent(typeSpecBuilder::addMethod);
        createNextIdMethod().ifPresent(typeSpecBuilder::addMethod);
        createInitMethod().ifPresent(typeSpecBuilder::addMethod);
    }

    protected Optional<MethodSpec> createConstructor() {
        return Optional.of(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(
                        CodeGenHelper.createParameterSpecBuilder(GenCreateCommand.class, "command")
                                .addAnnotation(Nonnull.class)
                                .build()
                )
                .addCode(
                        CodeBlock.of("super(command);")
                )
                .build());
    }

    protected Optional<MethodSpec> createCreateMethod() {
        return Optional.of(MethodSpec.methodBuilder("create")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(
                        CodeGenHelper.createParameterSpecBuilder(GenCreateCommand.class, "command")
                                .addAnnotation(Nonnull.class)
                                .build()
                )
                .addCode(
                        CodeBlock.of(
                                """
                                        final $T context = new $T(command);
                                        return context.init();
                                        """,
                                this.currentGeneratingClassName,
                                this.currentGeneratingClassName
                        )
                )
                .returns(this.currentGeneratingClassName)
                .build());
    }

    protected Optional<MethodSpec> createNextIdMethod() {
        return Optional.of(MethodSpec.methodBuilder("nextId")
                .addModifiers(Modifier.PUBLIC)
                .addCode(
                        CodeBlock.of(
                                " return $T.nextId();",
                                ClassName.get(IdHelper.class)
                        )
                )
                .returns(Long.class)
                .build());
    }

    protected Optional<MethodSpec> createInitMethod() {
        return Optional.of(MethodSpec.methodBuilder("init")
                .addModifiers(Modifier.PRIVATE)
                .addCode(CodeBlock.of(" // initialize the context here\n"))
                .addCode(CodeBlock.of(" return this;"))
                .returns(this.currentGeneratingClassName)
                .build());
    }
}
