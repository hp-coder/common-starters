package com.hp.codegen.modifier.converter;

import com.google.auto.common.MoreTypes;
import com.hp.codegen.modifier.FieldTypeConverter;
import com.hp.codegen.util.CodeGenHelper;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import java.util.Optional;

/**
 * @author hp
 */
public class BaseEnumToCodeTypeTypeConverter implements FieldTypeConverter {
    private DeclaredType declaredType;

    @Override
    public TypeName convert(VariableElement variableElement) {
        //对于BaseEnum接口, 第一个参数化类型为泛型本身, 第二个参数化类型为response返回类型, 一般是 String 或者 Integer
        return ClassName.bestGuess(MoreTypes.asTypeElement(declaredType.getTypeArguments().get(1)).getQualifiedName().toString());
    }

    @Override
    public boolean convertable(VariableElement ve) {
        final Optional<DeclaredType> baseEnumType = CodeGenHelper.getBaseEnumType(ve);
        baseEnumType.ifPresent(dt -> declaredType = dt);
        return baseEnumType.isPresent();
    }
}
