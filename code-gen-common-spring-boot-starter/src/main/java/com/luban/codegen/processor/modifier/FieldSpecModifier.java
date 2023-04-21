package com.luban.codegen.processor.modifier;

import com.squareup.javapoet.TypeName;

import javax.lang.model.element.VariableElement;

/**
 * @author hp 2023/4/21
 */
public interface FieldSpecModifier {

    TypeName modify(VariableElement variableElement);

    boolean isModifiable(VariableElement variableElement);
}
