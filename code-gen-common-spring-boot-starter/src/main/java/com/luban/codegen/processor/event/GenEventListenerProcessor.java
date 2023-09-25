package com.luban.codegen.processor.event;

import com.google.auto.service.AutoService;
import com.luban.codegen.constant.Orm;
import com.luban.codegen.processor.AbstractCodeGenProcessor;
import com.luban.codegen.spi.CodeGenProcessor;
import com.luban.codegen.util.StringUtils;
import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author hp
 */

@AutoService(CodeGenProcessor.class)
public class GenEventListenerProcessor extends AbstractCodeGenProcessor {

    public static final String SUFFIX = "EventProcessor";

    @Override
    protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        if (StringUtils.containsNull(getNameContext().getEventPackageName(), getNameContext().getEventClassName())) {
            return;
        }
        final TypeSpec.Builder builder = TypeSpec.classBuilder(typeElement.getSimpleName() + SUFFIX)
                .addAnnotation(Slf4j.class)
                .addAnnotation(Component.class)
                .addAnnotation(RequiredArgsConstructor.class)
                .addAnnotation(AnnotationSpec.builder(Transactional.class).addMember("rollbackFor", "$L", "Exception.class").build())
                .addModifiers(Modifier.PUBLIC);

        createdHandlerMethod(typeElement).ifPresent(builder::addMethod);
        updatedHandlerMethod(typeElement).ifPresent(builder::addMethod);

        generateJavaSourceFile(generatePackage(typeElement), generatePath(typeElement), builder);
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenEventListener.class;
    }

    @Override
    public String generatePackage(TypeElement typeElement) {
        return typeElement.getAnnotation(GenEventListener.class).pkgName();
    }

    @Override
    public String generatePath(TypeElement typeElement) {
        return typeElement.getAnnotation(GenEventListener.class).sourcePath();
    }

    private Optional<MethodSpec> createdHandlerMethod(TypeElement typeElement) {
        return Optional.of(
                MethodSpec.methodBuilder(String.format("handle%sCreated", typeElement.getSimpleName()))
                        .addAnnotation(EventListener.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(ParameterSpec.builder(
                                                ClassName.get(getNameContext().getEventPackageName(), getNameContext().getEventClassName(), GenEventProcessor.getCreatedEventName(typeElement)),
                                                "event"
                                        )
                                        .build()
                        )
                        .addCode(
                                CodeBlock.of(
                                        "final $T context = event.getContext();\n" +
                                                "final $T entity = context.getEntity();\n" +
                                                "final $T command = context.getCommand();\n" +
                                                "// handle event\n",
                                        ClassName.get(getNameContext().getCreateContextPackageName(), getNameContext().getCreateContextClassName()),
                                        ClassName.get(typeElement),
                                        ClassName.get(getNameContext().getCreateCommandPackageName(), getNameContext().getCreateCommandClassName())
                                )
                        )
                        .build()
        );
    }

    private Optional<MethodSpec> updatedHandlerMethod(TypeElement typeElement) {
        return Optional.of(
                MethodSpec.methodBuilder(String.format("handle%sUpdated", typeElement.getSimpleName()))
                        .addAnnotation(EventListener.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(ParameterSpec.builder(
                                                ClassName.get(getNameContext().getEventPackageName(), getNameContext().getEventClassName(), GenEventProcessor.getUpdatedEventName(typeElement)),
                                                "event"
                                        )
                                        .build()
                        )
                        .addCode(
                                CodeBlock.of(
                                        "final $T context = event.getContext();\n" +
                                                "final $T entity = context.getEntity();\n" +
                                                "final $T command = context.getCommand();\n" +
                                                "// handle event\n",
                                        ClassName.get(getNameContext().getUpdateContextPackageName(), getNameContext().getUpdateContextClassName()),
                                        ClassName.get(typeElement),
                                        ClassName.get(getNameContext().getUpdateCommandPackageName(), getNameContext().getUpdateCommandClassName())
                                )
                        )
                        .build()
        );
    }
}
