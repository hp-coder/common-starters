package com.hp.codegen.processor.model;

import com.google.auto.service.AutoService;
import com.hp.codegen.annotation.model.GenUpdateRequest;
import com.hp.codegen.context.CodeGenContextHolder;
import com.hp.codegen.spi.CodeGenProcessor;
import com.hp.codegen.util.CodeGenHelper;
import com.hp.common.base.model.Request;
import com.squareup.javapoet.FieldSpec;
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
 * @date 2022/10/24
 */
@AutoService(CodeGenProcessor.class)
public class GenUpdateRequestProcessor extends AbstractModelCodeGenProcessor {

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenUpdateRequest.class;
    }

    @Override
    protected void customizeType(TypeElement typeElement, RoundEnvironment roundEnvironment, TypeSpec.Builder typeSpecBuilder) {
        final List<VariableElement> fields = CodeGenHelper.findNotIgnoreAndNotDeprecatedFields(typeElement);

        typeSpecBuilder
                .addSuperinterface(Request.class)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Data.class)
                .addAnnotation(Validated.class)
                .addField(FieldSpec.builder(Long.class, "id", Modifier.PRIVATE).build());

        CodeGenHelper.createFields(typeSpecBuilder, fields, CodeGenContextHolder.getFieldCreator(this.currentGeneratingClassName, false));
    }
}
