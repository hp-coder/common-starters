package com.hp.codegen.processor.api;

import com.google.auto.service.AutoService;
import com.hp.codegen.annotation.api.GenQueryApi;
import com.hp.codegen.annotation.domain.GenMapper;
import com.hp.codegen.annotation.model.GenPageRequest;
import com.hp.codegen.annotation.model.GenPageResponse;
import com.hp.codegen.annotation.model.GenResponse;
import com.hp.codegen.context.CodeGenContextHolder;
import com.hp.codegen.spi.CodeGenProcessor;
import com.hp.codegen.util.CodeGenHelper;
import com.hp.common.base.model.PageRequestWrapper;
import com.hp.common.base.model.PageResponse;
import com.hp.common.base.model.Returns;
import com.squareup.javapoet.*;
import com.squareup.javapoet.TypeSpec.Builder;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
public class GenQueryApiProcessor extends AbstractApiCodeGenProcessor {

    @Override
    protected boolean isInterface() {
        return true;
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenQueryApi.class;
    }

    @Override
    protected void customizeType(TypeElement typeElement, RoundEnvironment roundEnvironment, Builder typeSpecBuilder) {
        typeSpecBuilder
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Validated.class);

        typeSpecBuilder.addField(
                FieldSpec.builder(String.class, "PATH", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("$S", CodeGenContextHolder.getCurrentTypeFieldName() + "/qry")
                        .build()
        );

        final String apiVersion = CodeGenHelper.getApiVersion(typeElement, GenQueryApi.class);

        createFindByIdMethod(apiVersion).ifPresent(typeSpecBuilder::addMethod);
        createFindByPageMethod(apiVersion).ifPresent(typeSpecBuilder::addMethod);
    }

    protected Optional<MethodSpec> createFindByIdMethod(String apiVersion) {
        if (CodeGenContextHolder.missingAnyAnnotated(
                GenResponse.class
        )) {
            CodeGenContextHolder.log(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    String.format("To generate findById() for %s requires @GenResponse.", this.currentGeneratingTypeName)
            );
            return Optional.empty();
        }
        final MethodSpec methodSpec = MethodSpec.methodBuilder("findById")
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
                        AnnotationSpec.builder(GetMapping.class)
                                .addMember("value", "$S", apiVersion + "/findById/{id}")
                                .build()
                )
                .returns(ParameterizedTypeName.get(ClassName.get(Returns.class), CodeGenContextHolder.getClassName(GenResponse.class)))
                .build();

        return Optional.of(methodSpec);
    }

    protected Optional<MethodSpec> createFindByPageMethod(String apiVersion) {
        if (CodeGenContextHolder.missingAnyAnnotated(
                GenPageRequest.class,
                GenPageResponse.class,
                GenMapper.class
        )) {
            CodeGenContextHolder.log(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    String.format("To generate findByPage() for %s requires @GenPageRequest @GenPageResponse and @GenMapper.", this.currentGeneratingTypeName)
            );
            return Optional.empty();
        }
        final MethodSpec methodSpec = MethodSpec.methodBuilder("findByPage")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(
                        ParameterSpec.builder(
                                        ParameterizedTypeName.get(
                                                ClassName.get(PageRequestWrapper.class),
                                                CodeGenContextHolder.getClassName(GenPageRequest.class)
                                        ),
                                        "requestWrapper"
                                )
                                .addAnnotation(RequestBody.class)
                                .addAnnotation(Valid.class)
                                .build()
                )
                .addAnnotation(
                        AnnotationSpec.builder(PostMapping.class)
                                .addMember("value", "$S", apiVersion + "/findByPage")
                                .build(

                                )
                )
                .returns(
                        ParameterizedTypeName.get(
                                ClassName.get(Returns.class),
                                ParameterizedTypeName.get(
                                        ClassName.get(PageResponse.class),
                                        CodeGenContextHolder.getClassName(GenPageResponse.class)
                                )
                        )
                )
                .build();
        return Optional.of(methodSpec);
    }
}
