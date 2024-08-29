package com.hp.codegen.processor.api;

import com.google.auto.service.AutoService;
import com.hp.codegen.annotation.api.GenCommandApi;
import com.hp.codegen.annotation.domain.GenCreateCommand;
import com.hp.codegen.annotation.domain.GenMapper;
import com.hp.codegen.annotation.domain.GenUpdateCommand;
import com.hp.codegen.annotation.model.GenCreateRequest;
import com.hp.codegen.annotation.model.GenUpdateRequest;
import com.hp.codegen.context.CodeGenContextHolder;
import com.hp.codegen.spi.CodeGenProcessor;
import com.hp.codegen.util.CodeGenHelper;
import com.hp.common.base.model.Returns;
import com.squareup.javapoet.*;
import com.squareup.javapoet.TypeSpec.Builder;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
public class GenCommandApiProcessor extends AbstractApiCodeGenProcessor {

    @Override
    protected boolean isInterface() {
        return true;
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenCommandApi.class;
    }

    @Override
    protected void customizeType(TypeElement typeElement, RoundEnvironment roundEnvironment, Builder typeSpecBuilder) {
        typeSpecBuilder
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Validated.class);

        typeSpecBuilder.addField(
                FieldSpec.builder(String.class, "PATH", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("$S", CodeGenContextHolder.getCurrentTypeFieldName() + "/cmd")
                        .build()
        );

        final String apiVersion = CodeGenHelper.getApiVersion(typeElement, GenCommandApi.class);

        createCreateMethod(apiVersion).ifPresent(typeSpecBuilder::addMethod);
//        createUpdateMethod().ifPresent(typeSpecBuilder::addMethod);
//        createEnableMethod().ifPresent(typeSpecBuilder::addMethod);
//        createDisableMethod().ifPresent(typeSpecBuilder::addMethod);
    }

    protected Optional<MethodSpec> createCreateMethod(String apiVersion) {
        if (
                CodeGenContextHolder.missingAnyAnnotated(
                        GenCreateRequest.class,
                        GenCreateCommand.class
                )
        ) {
            CodeGenContextHolder.log(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    String.format("To generate create%s() for %s requires @GenCreateRequest and @GenCreateCommand.", CodeGenContextHolder.getCurrentTypeName(), this.currentGeneratingTypeName)
            );
            return Optional.empty();
        }
        final MethodSpec methodSpec = MethodSpec.methodBuilder("create" + CodeGenContextHolder.getCurrentTypeName())
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(
                        CodeGenHelper.createParameterSpecBuilder(GenCreateRequest.class, "request")
                                .addAnnotation(RequestBody.class)
                                .addAnnotation(Valid.class)
                                .build()
                )
                .addAnnotation(
                        AnnotationSpec.builder(PostMapping.class)
                                .addMember("value", "$S", apiVersion + "/create")
                                .build()
                )
                .returns(ParameterizedTypeName.get(ClassName.get(Returns.class), ClassName.get(Void.class)))
                .build();

        return Optional.of(methodSpec);
    }

    protected Optional<MethodSpec> createUpdateMethod() {
        if (CodeGenContextHolder.missingAnyAnnotated(
                GenUpdateRequest.class,
                GenUpdateCommand.class,
                GenMapper.class
        )) {
            CodeGenContextHolder.log(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    String.format("To generate create%s() for %s requires @GenUpdateRequest @GenUpdateCommand and @GenMapper.", CodeGenContextHolder.getCurrentTypeName(), this.currentGeneratingTypeName)
            );
            return Optional.empty();
        }
        final MethodSpec methodSpec = MethodSpec.methodBuilder("update" + CodeGenContextHolder.getCurrentTypeName())
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(
                        CodeGenHelper.createParameterSpecBuilder(GenUpdateRequest.class, "request")
                                .addAnnotation(RequestBody.class)
                                .addAnnotation(Valid.class)
                                .build()
                )
                .addAnnotation(
                        AnnotationSpec.builder(PostMapping.class)
                                .addMember("value", "$S", "update")
                                .build()
                )
                .returns(ParameterizedTypeName.get(ClassName.get(Returns.class), ClassName.get(Void.class)))
                .build();

        return Optional.of(methodSpec);
    }

    protected Optional<MethodSpec> createEnableMethod() {
        final MethodSpec methodSpec = MethodSpec.methodBuilder("enable" + CodeGenContextHolder.getCurrentTypeName())
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(
                        ParameterSpec.builder(Long.class, "id")
                                .addAnnotation(
                                        AnnotationSpec.builder(PathVariable.class)
                                                .addMember("value", "$S", "id")
                                                .build()
                                )
                                .build()
                )
                .addAnnotation(
                        AnnotationSpec.builder(PostMapping.class)
                                .addMember("value", "$S", "enable/{id}")
                                .build()
                )
                .returns(ParameterizedTypeName.get(ClassName.get(Returns.class), ClassName.get(Void.class)))
                .build();

        return Optional.of(methodSpec);
    }

    protected Optional<MethodSpec> createDisableMethod() {
        final MethodSpec methodSpec = MethodSpec.methodBuilder("disable" + CodeGenContextHolder.getCurrentTypeName())
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(
                        ParameterSpec.builder(Long.class, "id")
                                .addAnnotation(
                                        AnnotationSpec.builder(PathVariable.class)
                                                .addMember("value", "$S", "id")
                                                .build()
                                )
                                .build()
                )
                .addAnnotation(
                        AnnotationSpec.builder(PostMapping.class)
                                .addMember("value", "$S", "disable/{id}")
                                .build()
                )
                .returns(ParameterizedTypeName.get(ClassName.get(Returns.class), ClassName.get(Void.class)))
                .build();
        return Optional.of(methodSpec);
    }
}
