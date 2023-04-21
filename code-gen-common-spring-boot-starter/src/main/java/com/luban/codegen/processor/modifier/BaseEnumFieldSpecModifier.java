package com.luban.codegen.processor.modifier;

import com.google.auto.common.MoreTypes;
import com.luban.common.base.enums.BaseEnum;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.Optional;

/**
 * @author hp 2023/4/21
 */
public class BaseEnumFieldSpecModifier implements FieldSpecModifier {
    private DeclaredType declaredType;

    @Override
    public TypeName modify(VariableElement variableElement) {
        //对于BaseEnum接口, 第一个参数化类型为泛型本身, 第二个参数化类型为response返回类型, 一般是 String 或者 Integer
        return ClassName.bestGuess(MoreTypes.asTypeElement(declaredType.getTypeArguments().get(1)).getQualifiedName().toString());
    }

    @Override
    public boolean isModifiable(VariableElement ve) {
        final TypeMirror type = ve.asType();
        if (type.getKind().isPrimitive()) {
            return false;
        }
        final TypeElement typeElement = MoreTypes.asTypeElement(ve.asType());
        if (typeElement.getKind() != ElementKind.ENUM) {
            return false;
        }
        final Optional<DeclaredType> any = typeElement.getInterfaces()
                .stream()
                .map(MoreTypes::asDeclared)
                .filter(dt -> MoreTypes.isTypeOf(BaseEnum.class, dt))
                .findAny();
        any.ifPresent(dt -> declaredType = dt);
        return any.isPresent();
    }
}
