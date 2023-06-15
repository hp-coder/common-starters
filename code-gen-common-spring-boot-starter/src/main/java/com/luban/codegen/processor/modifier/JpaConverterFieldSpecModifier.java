package com.luban.codegen.processor.modifier;

import com.google.auto.common.MoreTypes;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Convert;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.Objects;
import java.util.Optional;

/**
 * @author hp 2023/4/21
 */
public class JpaConverterFieldSpecModifier implements FieldSpecModifier {

    private DeclaredType declaredType;

    @Override
    public TypeName modify(VariableElement variableElement) {
        // 针对 jpa的convert注解特殊处理, 拿到converter类和其接口泛型的第二个参数, 为实际返回类型
        return ClassName.bestGuess(MoreTypes.asTypeElement(declaredType.getTypeArguments().get(1)).getQualifiedName().toString());
    }

    @Override
    public boolean isModifiable(VariableElement ve) {
        final TypeMirror type = ve.asType();
        if (type.getKind().isPrimitive()) {
            return false;
        }
        final Convert convert = ve.getAnnotation(Convert.class);
        if (convert == null) {
            return false;
        }
        final Optional<? extends AnnotationMirror> convertAnnotation = ve.getAnnotationMirrors().stream()
                .filter(am -> MoreTypes.isTypeOf(Convert.class, am.getAnnotationType()))
                .findFirst();
        if (!convertAnnotation.isPresent()) {
            return false;
        }
        final Optional<TypeMirror> converter = convertAnnotation.get()
                .getElementValues()
                .entrySet()
                .stream()
                .filter(entry -> Objects.equals("converter", entry.getKey().getSimpleName().toString()))
                .map(entry -> (TypeMirror) entry.getValue().getValue())
                .findFirst();
        if (!converter.isPresent()){
            return false;
        }
        final TypeMirror typeMirror = converter.get();
        final Optional<DeclaredType> any = MoreTypes.asTypeElement(typeMirror).getInterfaces()
                .stream()
                .map(MoreTypes::asDeclared)
                .filter(dt -> MoreTypes.isTypeOf(AttributeConverter.class, dt))
                .findAny();
        any.ifPresent(dt -> declaredType = dt);
        return any.isPresent();
    }
}
