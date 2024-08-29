package com.hp.codegen.processor.api;

import cn.hutool.core.util.StrUtil;
import com.google.auto.service.AutoService;
import com.hp.codegen.annotation.api.GenApi;
import com.hp.codegen.annotation.api.GenCommandApi;
import com.hp.codegen.annotation.api.GenCommandFeignClient;
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
public class GenCommandFeignProcessor extends AbstractApiCodeGenProcessor {

    @Override
    protected boolean isInterface() {
        return true;
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenCommandFeignClient.class;
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
    protected void customizeType(TypeElement typeElement, RoundEnvironment roundEnvironment, Builder typeSpecBuilder) {
        final String classFieldName = StrUtil.lowerFirst(CodeGenContextHolder.getCurrentTypeName());

        final String serverName = Optional.ofNullable(typeElement.getAnnotation(GenApi.class))
                .map(GenApi::serverName)
                .orElse(Optional.ofNullable(typeElement.getAnnotation(GenCommandFeignClient.class)).map(GenCommandFeignClient::serverName).orElse(""));

        typeSpecBuilder
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Validated.class)
                .addAnnotation(
                        AnnotationSpec
                                .builder(FeignClient.class)
                                .addMember("value", "$S", serverName)
                                .addMember("contextId", "$S", classFieldName + "Client")
                                .addMember("path", "$T.PATH", CodeGenContextHolder.getClassName(GenCommandApi.class))
                                .build()
                )
                .addSuperinterface(
                        CodeGenContextHolder.getClassName(GenCommandApi.class)
                );
    }
}
