package com.hp.codegen.modifier.converter;

import com.hp.codegen.modifier.FieldTypeConverter;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.VariableElement;

/**
 * @author hp
 */
public class BaseEnumNoOpTypeConverter implements FieldTypeConverter {

    @Override
    public TypeName convert(VariableElement variableElement) {
        return TypeName.get(variableElement.asType());
    }

    @Override
    public boolean convertable(VariableElement ve) {
        return true;
    }
}
