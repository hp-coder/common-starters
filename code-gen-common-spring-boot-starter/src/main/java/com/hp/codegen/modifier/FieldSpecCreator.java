package com.hp.codegen.modifier;

import com.squareup.javapoet.FieldSpec;

import javax.lang.model.element.VariableElement;
import java.util.List;

/**
 * @author hp
 */
public interface FieldSpecCreator {

    List<FieldSpec> createFields(VariableElement fieldVariableElement);

}
