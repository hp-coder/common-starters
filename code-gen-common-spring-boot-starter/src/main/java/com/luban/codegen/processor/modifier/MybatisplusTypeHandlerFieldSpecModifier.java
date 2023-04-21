
package com.luban.codegen.processor.modifier;

import com.squareup.javapoet.TypeName;

import javax.lang.model.element.VariableElement;

/**
 * @author hp 2023/4/21
 */
public class MybatisplusTypeHandlerFieldSpecModifier implements FieldSpecModifier{
    @Override
    public TypeName modify(VariableElement variableElement) {
        return null;
    }

    @Override
    public boolean isModifiable(VariableElement variableElement) {
        return false;
    }
}
