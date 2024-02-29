package com.hp.codegen.processor.modifier;

import cn.hutool.core.util.BooleanUtil;
import com.google.auto.common.MoreTypes;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/**
 * @author hp 2023/4/21
 */
public class LongToStringFieldSpecModifier extends DefaultToStringFieldSpecModifier {
    @Override
    public boolean isModifiable(VariableElement ve) {
        final TypeMirror type = ve.asType();
        final TypeKind kind = type.getKind();
        final boolean a = MoreTypes.isTypeOf(Long.class, type);
        final boolean b = kind.isPrimitive() && kind == TypeKind.LONG;
        return BooleanUtil.or(a, b) || super.isModifiable(ve);
    }
}
