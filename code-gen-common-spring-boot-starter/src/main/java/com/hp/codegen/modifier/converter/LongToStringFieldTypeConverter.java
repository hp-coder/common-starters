package com.hp.codegen.modifier.converter;

import com.google.auto.common.MoreTypes;
import com.hp.codegen.modifier.FieldTypeConverter;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/**
 * @author hp
 */
public class LongToStringFieldTypeConverter implements FieldTypeConverter {
    @Override
    public TypeName convert(VariableElement variableElement) {
        return ClassName.get(String.class);
    }

    @Override
    public boolean convertable(VariableElement ve) {
        final TypeMirror type = ve.asType();
        final TypeKind kind = type.getKind();
        return MoreTypes.isTypeOf(Long.class, type) || kind.isPrimitive() && kind == TypeKind.LONG;
    }
}
