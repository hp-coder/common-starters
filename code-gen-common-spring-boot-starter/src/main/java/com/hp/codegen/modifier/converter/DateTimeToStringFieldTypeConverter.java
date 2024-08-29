
package com.hp.codegen.modifier.converter;

import cn.hutool.core.util.BooleanUtil;
import com.google.auto.common.MoreTypes;
import com.hp.codegen.modifier.FieldTypeConverter;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author hp
 */
public class DateTimeToStringFieldTypeConverter implements FieldTypeConverter {
    @Override
    public TypeName convert(VariableElement variableElement) {
        return ClassName.get(String.class);
    }

    @Override
    public boolean convertable(VariableElement ve) {
        final TypeMirror type = ve.asType();
        final boolean a = MoreTypes.isTypeOf(LocalDateTime.class, type);
        final boolean b = MoreTypes.isTypeOf(LocalDate.class, type);
        final boolean c = MoreTypes.isTypeOf(Instant.class, type);
        final boolean d = MoreTypes.isTypeOf(Date.class, type);
        return BooleanUtil.or(a, b, c, d);
    }
}
