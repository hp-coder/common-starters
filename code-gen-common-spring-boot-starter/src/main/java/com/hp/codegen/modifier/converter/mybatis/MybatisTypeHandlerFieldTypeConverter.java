
package com.hp.codegen.modifier.converter.mybatis;

import com.baomidou.mybatisplus.annotation.TableField;
import com.google.auto.common.MoreTypes;
import com.hp.codegen.modifier.FieldTypeConverter;
import com.hp.mybatisplus.converter.TypeHandlerAdapter;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.Objects;
import java.util.Optional;

/**
 * @author hp 2023/4/21
 */
public class MybatisTypeHandlerFieldTypeConverter implements FieldTypeConverter {

    private DeclaredType declaredType;

    @Override
    public TypeName convert(VariableElement variableElement) {
        // 针对 mpb的typeHandler注解特殊处理, 拿到TypeHandlerAdapter类和其接口泛型的第二个参数, 为实际返回类型
        return ClassName.bestGuess(MoreTypes.asTypeElement(declaredType.getTypeArguments().get(1))
                .getQualifiedName().toString());
    }

    @Override
    public boolean convertable(VariableElement ve) {
        final TypeMirror type = ve.asType();
        if (type.getKind().isPrimitive()) {
            return false;
        }
        final TableField tableField = ve.getAnnotation(TableField.class);
        if (tableField == null) {
            return false;
        }
        final Optional<? extends AnnotationMirror> convertAnnotation = ve.getAnnotationMirrors().stream()
                .filter(am -> MoreTypes.isTypeOf(TableField.class, am.getAnnotationType()))
                .findFirst();
        if (convertAnnotation.isEmpty()) {
            return false;
        }
        final Optional<TypeMirror> converter = convertAnnotation.get()
                .getElementValues()
                .entrySet()
                .stream()
                .filter(entry -> Objects.equals("typeHandler", entry.getKey().getSimpleName().toString()))
                .map(entry -> (TypeMirror) entry.getValue().getValue())
                .findFirst();
        if (converter.isEmpty()){
            return false;
        }
        final TypeMirror typeMirror = converter.get();
        final Optional<DeclaredType> any = MoreTypes.asTypeElement(typeMirror).getInterfaces()
                .stream()
                .map(MoreTypes::asDeclared)
                .filter(dt -> MoreTypes.isTypeOf(TypeHandlerAdapter.class, dt))
                .findAny();
        any.ifPresent(dt -> declaredType = dt);
        return Objects.nonNull(declaredType);
    }
}
