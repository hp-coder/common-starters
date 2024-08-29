package com.hp.codegen.modifier.customizer;

import com.hp.codegen.modifier.FieldSpecCustomizer;
import com.hp.common.base.annotation.FieldDesc;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.VariableElement;
import java.util.Optional;

/**
 * @author hp
 */
public class DefaultFieldSpecCustomizer implements FieldSpecCustomizer {

    @Override
    public void customizeField(ClassName currentGeneratingClassName, VariableElement fieldVariableElement, TypeName convertedFieldTypeName, FieldSpec.Builder fieldSpecBuilder) {
        Optional.ofNullable(fieldVariableElement.getAnnotation(FieldDesc.class))
                .ifPresent(an ->
                        fieldSpecBuilder.addAnnotation(
                                AnnotationSpec.builder(FieldDesc.class)
                                        .addMember("value", "$S", an.value())
                                        .build()
                        )
                );
    }
}
