package com.hp.codegen.modifier;


import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.VariableElement;

/**
 * @author hp
 */
@FunctionalInterface
public interface FieldSpecCustomizer {

    void customizeField(ClassName currentGeneratingClassName, VariableElement fieldVariableElement, TypeName convertedFieldTypeName, FieldSpec.Builder fieldSpecBuilder);

}
