package com.hp.codegen.processor.app;


import com.google.auto.service.AutoService;
import com.hp.codegen.annotation.app.GenCommandAppService;
import com.hp.codegen.annotation.domain.GenCreateCommand;
import com.hp.codegen.context.CodeGenContextHolder;
import com.hp.codegen.spi.CodeGenProcessor;
import com.hp.codegen.util.CodeGenHelper;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

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
public class GenCommandAppServiceProcessor extends AbstractAppCodeGenProcessor {
    @Override
    protected boolean isInterface() {
        return true;
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenCommandAppService.class;
    }

    @Override
    protected void customizeType(TypeElement typeElement, RoundEnvironment roundEnvironment, TypeSpec.Builder typeSpecBuilder) {
        typeSpecBuilder
                .addModifiers(Modifier.PUBLIC);

        createCreateMethod().ifPresent(typeSpecBuilder::addMethod);
    }

    protected Optional<MethodSpec> createCreateMethod() {
        final String methodName = "create" + CodeGenContextHolder.getCurrentTypeName();
        if (!CodeGenContextHolder.isAnnotationPresent(GenCreateCommand.class)) {
            CodeGenContextHolder.log(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    String.format("To generate %s() on %s requires @GenCreateCommand.", methodName, this.currentGeneratingTypeName)
            );
            return Optional.empty();
        }

        final MethodSpec methodSpec = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(
                        CodeGenHelper.createParameterSpecBuilder(GenCreateCommand.class, "command")
                                .build()
                )
                .build();

        return Optional.of(methodSpec);
    }

}
