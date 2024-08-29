package com.hp.codegen.modifier;

import com.squareup.javapoet.TypeName;

import javax.lang.model.element.VariableElement;

/**
 * First matched
 *
 * @author hp
 */
public interface FieldTypeConverter {

    TypeName convert(VariableElement variableElement);

    boolean convertable(VariableElement variableElement);
}
