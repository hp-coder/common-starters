package com.hp.codegen.processor.domain;

import com.google.auto.service.AutoService;
import com.hp.codegen.annotation.domain.GenCreateCommand;
import com.hp.codegen.spi.CodeGenProcessor;
import com.hp.codegen.util.CodeGenHelper;
import com.hp.common.base.command.Command;
import com.squareup.javapoet.TypeSpec;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author hp
 */
@AutoService(value = CodeGenProcessor.class)
public class GenCreateCommandProcessor extends AbstractDomainCodeGenProcessor {

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenCreateCommand.class;
    }

    @Override
    protected void customizeType(TypeElement typeElement, RoundEnvironment roundEnvironment, TypeSpec.Builder typeSpecBuilder) {
        final List<VariableElement> fields = CodeGenHelper.findNotIgnoreAndNotDeprecatedFields(typeElement);

        typeSpecBuilder
                .addSuperinterface(Command.class)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Validated.class)
                .addAnnotation(Data.class);

        CodeGenHelper.createFields(typeSpecBuilder, fields, this.currentGeneratingClassName);
    }
}
