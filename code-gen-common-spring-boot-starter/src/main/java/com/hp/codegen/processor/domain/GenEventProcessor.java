package com.hp.codegen.processor.domain;

import cn.hutool.core.util.StrUtil;
import com.google.auto.service.AutoService;
import com.hp.codegen.annotation.domain.GenEvent;
import com.hp.codegen.context.CodeGenContextHolder;
import com.hp.codegen.spi.CodeGenProcessor;
import com.hp.common.base.event.DomainEvent;
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
public class GenEventProcessor extends AbstractDomainCodeGenProcessor {

    public static final String CREATED_SUFFIX = "CreatedEvent";
    public static final String UPDATED_SUFFIX = "UpdatedEvent";

    public static String getCreatedEventName(TypeElement typeElement) {
        return typeElement.getSimpleName() + CREATED_SUFFIX;
    }

    public static String getUpdatedEventName(TypeElement typeElement) {
        return typeElement.getSimpleName() + UPDATED_SUFFIX;
    }

    @Override
    protected boolean isInterface() {
        return true;
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenEvent.class;
    }

    @Override
    protected void customizeType(TypeElement typeElement, RoundEnvironment roundEnvironment, TypeSpec.Builder typeSpecBuilder) {
        typeSpecBuilder
                .addModifiers(Modifier.PUBLIC);

        createCreatedEventClass().ifPresent(typeSpecBuilder::addType);
        createUpdatedEventClass().ifPresent(typeSpecBuilder::addType);
    }

    protected Optional<TypeSpec> createCreatedEventClass() {
        final TypeSpec typeSpec = TypeSpec.classBuilder(getCreatedEventName(CodeGenContextHolder.getCurrentTypeElement()))
                .addAnnotation(Value.class)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addSuperinterface(DomainEvent.class)
                .addField(
                        FieldSpec.builder(CodeGenContextHolder.getCurrentTypeClassName(), StrUtil.lowerFirst(CodeGenContextHolder.getCurrentTypeName()))
                                .build()
                )
                .build();
        return Optional.of(typeSpec);
    }

    protected Optional<TypeSpec> createUpdatedEventClass() {
        final TypeSpec typeSpec = TypeSpec.classBuilder(getUpdatedEventName(CodeGenContextHolder.getCurrentTypeElement()))
                .addAnnotation(Value.class)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addSuperinterface(DomainEvent.class)
                .addField(
                        FieldSpec.builder(CodeGenContextHolder.getCurrentTypeClassName(), StrUtil.lowerFirst(CodeGenContextHolder.getCurrentTypeName()))
                                .build()
                )
                .build();
        return Optional.of(typeSpec);
    }

}
