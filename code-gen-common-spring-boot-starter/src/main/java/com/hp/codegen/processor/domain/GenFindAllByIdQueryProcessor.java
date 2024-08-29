package com.hp.codegen.processor.domain;

import com.google.auto.service.AutoService;
import com.hp.codegen.annotation.domain.GenFindAllByIdQuery;
import com.hp.codegen.spi.CodeGenProcessor;
import com.hp.common.base.query.Query;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * @author hp
 */
@AutoService(value = CodeGenProcessor.class)
public class GenFindAllByIdQueryProcessor extends AbstractDomainCodeGenProcessor {

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenFindAllByIdQuery.class;
    }

    @Override
    protected void customizeType(TypeElement typeElement, RoundEnvironment roundEnvironment, TypeSpec.Builder typeSpecBuilder) {

        typeSpecBuilder
                .addSuperinterface(Query.class)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Validated.class)
                .addAnnotation(Data.class)
                .addAnnotation(NoArgsConstructor.class)
                .addAnnotation(AllArgsConstructor.class);

        typeSpecBuilder.addField(FieldSpec.builder(ParameterizedTypeName.get(Collection.class, Long.class), "ids", Modifier.PRIVATE).build());
    }
}
