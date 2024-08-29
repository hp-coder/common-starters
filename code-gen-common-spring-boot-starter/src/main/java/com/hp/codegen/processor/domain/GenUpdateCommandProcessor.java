package com.hp.codegen.processor.domain;

import com.google.auto.service.AutoService;
import com.hp.codegen.annotation.domain.GenUpdateCommand;
import com.hp.codegen.context.CodeGenContextHolder;
import com.hp.codegen.spi.CodeGenProcessor;
import com.hp.codegen.util.CodeGenHelper;
import com.hp.common.base.command.Command;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;

/**
 * @author hp
 */
@AutoService(value = CodeGenProcessor.class)
public class GenUpdateCommandProcessor extends AbstractDomainCodeGenProcessor {

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenUpdateCommand.class;
    }

    @Override
    protected void customizeType(TypeElement typeElement, RoundEnvironment roundEnvironment, TypeSpec.Builder typeSpecBuilder) {
        final List<VariableElement> fields = CodeGenHelper.findNotIgnoreAndNotDeprecatedFields(typeElement);

        typeSpecBuilder
                .addSuperinterface(Command.class)
                .addAnnotation(Validated.class)
                .addAnnotation(Data.class)
                .addModifiers(Modifier.PUBLIC);

        createUpdaterMethod(fields).ifPresent(typeSpecBuilder::addMethod);

        CodeGenHelper.createFields(typeSpecBuilder, fields, this.currentGeneratingClassName);
    }

    protected Optional<MethodSpec> createUpdaterMethod(List<VariableElement> fields) {
        final String methodName = "update";
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(methodName)
                .addParameter(TypeName.get(CodeGenContextHolder.getCurrentTypeElement().asType()), "source")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class);

        fields.forEach(f -> methodBuilder.addStatement("$T.ofNullable(this.get$L()).ifPresent(source::set$L)", Optional.class, CodeGenHelper.getFieldMethodName(f), CodeGenHelper.getFieldMethodName(f)));

        final MethodSpec methodSpec = methodBuilder
                .build();

        return Optional.of(methodSpec);
    }
}
