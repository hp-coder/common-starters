package com.hp.codegen.processor.api;

import com.google.auto.service.AutoService;
import com.hp.codegen.annotation.api.GenApi;
import com.hp.codegen.annotation.api.GenQueryApi;
import com.hp.codegen.annotation.api.GenQueryFeignClient;
import com.hp.codegen.context.CodeGenContextHolder;
import com.hp.codegen.spi.CodeGenProcessor;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.TypeSpec.Builder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;

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
public class GenQueryFeignProcessor extends AbstractApiCodeGenProcessor {

    @Override
    protected boolean isInterface() {
        return true;
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenQueryFeignClient.class;
    }

    @Override
    protected boolean creatable(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        if (CodeGenContextHolder.missingAnyAnnotated(GenQueryApi.class)) {
            CodeGenContextHolder.log(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    String.format("To generate %s requires @GenQueryApi.", this.currentGeneratingTypeName)
            );
            return false;
        }
        return true;
    }

    @Override
    protected void customizeType(TypeElement typeElement, RoundEnvironment roundEnvironment, Builder typeSpecBuilder) {
        final String serverName = Optional.ofNullable(typeElement.getAnnotation(GenApi.class))
                .map(GenApi::serverName)
                .orElse(Optional.ofNullable(typeElement.getAnnotation(GenQueryFeignClient.class)).map(GenQueryFeignClient::serverName).orElse(""));

        typeSpecBuilder
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Validated.class)
                .addAnnotation(
                        AnnotationSpec
                                .builder(FeignClient.class)
                                .addMember("value", "$S", serverName)
                                .addMember("contextId", "$S", CodeGenContextHolder.getCurrentTypeFieldName() + "Client")
                                .addMember("path", "$T.PATH", CodeGenContextHolder.getClassName(GenQueryApi.class))
                                .build()
                )
                .addSuperinterface(
                        CodeGenContextHolder.getClassName(GenQueryApi.class)
                );
    }

}
