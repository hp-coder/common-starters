package com.hp.codegen.processor.feignservice;

import com.google.auto.service.AutoService;
import com.google.common.collect.Lists;
import com.hp.codegen.annotation.api.GenCommandApi;
import com.hp.codegen.annotation.app.GenCommandAppService;
import com.hp.codegen.annotation.domain.GenCreateCommand;
import com.hp.codegen.annotation.domain.GenMapper;
import com.hp.codegen.annotation.domain.GenUpdateCommand;
import com.hp.codegen.annotation.model.GenCreateRequest;
import com.hp.codegen.annotation.model.GenUpdateRequest;
import com.hp.codegen.annotation.feignservice.GenCommandFeignService;
import com.hp.codegen.context.CodeGenContextHolder;
import com.hp.codegen.spi.CodeGenProcessor;
import com.hp.codegen.util.CodeGenHelper;
import com.hp.common.base.model.Returns;
import com.squareup.javapoet.*;
import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
public class GenCommandFeignServiceProcessor extends AbstractFeignServiceCodeGenProcessor {

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenCommandFeignService.class;
    }

    @Override
    protected boolean creatable(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        if (CodeGenContextHolder.missingAnyAnnotated(GenCommandApi.class)) {
            CodeGenContextHolder.log(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    String.format("To generate %s requires @GenCommandApi.", this.currentGeneratingTypeName)
            );
            return false;
        }
        return true;
    }

    @Override
    protected void customizeType(TypeElement typeElement, RoundEnvironment roundEnvironment, TypeSpec.Builder typeSpecBuilder) {
        typeSpecBuilder
                .addAnnotation(Slf4j.class)
                .addAnnotation(Validated.class)
                .addAnnotation(RestController.class)
                .addAnnotation(RequiredArgsConstructor.class)
                .addAnnotation(
                        AnnotationSpec.builder(RequestMapping.class)
                                .addMember("value", "$T.PATH", CodeGenContextHolder.getClassName(GenCommandApi.class))
                                .build()
                )
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(CodeGenContextHolder.getClassName(GenCommandApi.class));

        final List<FieldSpec> injectionFields = createInjectionFields();
        typeSpecBuilder.addFields(injectionFields);

        createCreateMethod().ifPresent(typeSpecBuilder::addMethod);
//        createUpdateMethod().ifPresent(typeSpecBuilder::addMethod);
//        createEnableMethod().ifPresent(typeSpecBuilder::addMethod);
//        createDisableMethod().ifPresent(typeSpecBuilder::addMethod);
    }

    @Nonnull
    private static List<FieldSpec> createInjectionFields() {
        final List<FieldSpec> fieldSpecs = Lists.newArrayList();
        if (CodeGenContextHolder.isAnnotationPresent(GenCommandAppService.class)) {
            final FieldSpec serviceInjectionField = CodeGenHelper.createFieldSpecBuilder(GenCommandAppService.class)
                    .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                    .build();
            fieldSpecs.add(serviceInjectionField);
        }

        return fieldSpecs;
    }

    private Optional<MethodSpec> createCreateMethod() {
        if (
                CodeGenContextHolder.missingAnyAnnotated(
                        GenCommandAppService.class,
                        GenCreateRequest.class,
                        GenCreateCommand.class,
                        GenMapper.class
                )
        ) {
            CodeGenContextHolder.log(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    String.format("To generate create%s() for %s requires @GenCommandAppService @GenCreateRequest @GenCreateCommand and @GenMapper.", CodeGenContextHolder.getCurrentTypeName(), this.currentGeneratingTypeName)
            );
            return Optional.empty();
        }

        final MethodSpec methodSpec = MethodSpec.methodBuilder("create" + CodeGenContextHolder.getCurrentTypeName())
                .addModifiers(Modifier.PUBLIC)
                .addParameter(
                        CodeGenHelper.createParameterSpecBuilder(GenCreateRequest.class, "request")
//                                .addAnnotation(RequestBody.class)
                                .addAnnotation(Valid.class)
                                .build()
                )
                .addCode(
                        CodeBlock.of(
                                "final $T command = $T.INSTANCE.requestToCreateCommand(request);\n",
                                CodeGenContextHolder.getClassName(GenCreateCommand.class),
                                CodeGenContextHolder.getClassName(GenMapper.class)
                        )
                )
                .addCode(CodeBlock.of(
                        "$L.create$L(command);\n",
                        CodeGenContextHolder.getClassFieldName(GenCommandAppService.class),
                        CodeGenContextHolder.getCurrentTypeName()
                ))
                .addCode(
                        CodeBlock.of(
                                "return $T.success();",
                                ClassName.get(Returns.class)
                        )
                )
                .returns(ParameterizedTypeName.get(ClassName.get(Returns.class), ClassName.get(Void.class)))
                .build();
        return Optional.of(methodSpec);
    }

    private Optional<MethodSpec> createUpdateMethod() {
        if (CodeGenContextHolder.missingAnyAnnotated(
                GenCommandAppService.class,
                GenUpdateRequest.class,
                GenUpdateCommand.class,
                GenMapper.class
        )) {
            CodeGenContextHolder.log(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    String.format("To generate update%s() for %s requires @GenCommandAppService @GenUpdateRequest @GenUpdateCommand and @GenMapper.", CodeGenContextHolder.getCurrentTypeName(), this.currentGeneratingTypeName)
            );
            return Optional.empty();
        }
        final MethodSpec methodSpec = MethodSpec.methodBuilder("update" + CodeGenContextHolder.getCurrentTypeName())
                .addModifiers(Modifier.PUBLIC)
                .addParameter(
                        CodeGenHelper.createParameterSpecBuilder(GenUpdateRequest.class, "request")
//                                .addAnnotation(RequestBody.class)
                                .addAnnotation(Valid.class)
                                .build()
                )
                .addCode(
                        CodeBlock.of(
                                "final $T command = $T.INSTANCE.requestToUpdateCommand(request);\n",
                                CodeGenContextHolder.getClassName(GenUpdateCommand.class),
                                CodeGenContextHolder.getClassName(GenMapper.class)
                        )
                )
                .addCode(
                        CodeBlock.of(
                                "$L.update$L(command);\n",
                                CodeGenContextHolder.getClassFieldName(GenCommandAppService.class),
                                CodeGenContextHolder.getCurrentTypeName()
                        )
                )
                .addCode(
                        CodeBlock.of(
                                "return $T.success();",
                                ClassName.get(Returns.class)
                        )
                )
                .returns(
                        ParameterizedTypeName.get(
                                ClassName.get(Returns.class),
                                ClassName.get(Void.class)
                        )
                )
                .build();
        return Optional.of(methodSpec);
    }

    private Optional<MethodSpec> createEnableMethod() {
        if (!CodeGenContextHolder.isAnnotationPresent(GenCommandAppService.class)) {
            CodeGenContextHolder.log(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    String.format("To generate enable%s() for %s requires @GenCommandAppService.", CodeGenContextHolder.getCurrentTypeName(), this.currentGeneratingTypeName)
            );
            return Optional.empty();
        }

        final MethodSpec methodSpec = MethodSpec.methodBuilder("enable" + CodeGenContextHolder.getCurrentTypeName())
                .addModifiers(Modifier.PUBLIC)
                .addParameter(
                        ParameterSpec.builder(Long.class, "id")
//                                .addAnnotation(PathVariable.class)
                                .build()
                )
                .addCode(
                        CodeBlock.of(
                                "$L.enable$L(id);\n",
                                CodeGenContextHolder.getClassFieldName(GenCommandAppService.class),
                                CodeGenContextHolder.getCurrentTypeName()
                        )
                )
                .addCode(
                        CodeBlock.of(
                                "return $T.success();",
                                ClassName.get(Returns.class)
                        )
                )
                .returns(
                        ParameterizedTypeName.get(
                                ClassName.get(Returns.class),
                                ClassName.get(Void.class)
                        )
                )
                .build();
        return Optional.of(methodSpec);
    }

    private Optional<MethodSpec> createDisableMethod() {
        if (!CodeGenContextHolder.isAnnotationPresent(GenCommandAppService.class)) {
            CodeGenContextHolder.log(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    String.format("To generate disable%s() for %s requires @GenCommandAppService.", CodeGenContextHolder.getCurrentTypeName(), this.currentGeneratingTypeName)
            );
            return Optional.empty();
        }
        final MethodSpec methodSpec = MethodSpec.methodBuilder("disable" + CodeGenContextHolder.getCurrentTypeName())
                .addModifiers(Modifier.PUBLIC)
                .addParameter(
                        ParameterSpec.builder(Long.class, "id")
//                                .addAnnotation(PathVariable.class)
                                .build()
                )
                .addCode(
                        CodeBlock.of(
                                "$L.disable$L(id);\n",
                                CodeGenContextHolder.getClassFieldName(GenCommandAppService.class),
                                CodeGenContextHolder.getCurrentTypeName()
                        )
                )
                .addCode(
                        CodeBlock.of(
                                "return $T.success();",
                                ClassName.get(Returns.class)
                        )
                )
                .returns(
                        ParameterizedTypeName.get(
                                ClassName.get(Returns.class),
                                ClassName.get(Void.class)
                        )
                )
                .build();
        return Optional.of(methodSpec);
    }

}
