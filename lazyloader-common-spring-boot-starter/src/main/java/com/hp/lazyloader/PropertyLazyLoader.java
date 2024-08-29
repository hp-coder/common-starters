package com.hp.lazyloader;

import com.hp.common.base.utils.SpELHelper;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * @author hp
 */
@NoArgsConstructor
@Getter
public class PropertyLazyLoader {

    private Field field;

    private SpELHelper.StandardSpELGetter<Object, Object> spELGetter;

    public PropertyLazyLoader(Field field, SpELHelper.StandardSpELGetter<Object, Object> spELGetter) {
        this.field = field;
        this.spELGetter = spELGetter;
    }

    public static PropertyLazyLoader create(Field field, SpELHelper.StandardSpELGetter<Object, Object> spELGetter) {
        return new PropertyLazyLoader(Objects.requireNonNull(field), Objects.requireNonNull(spELGetter));
    }
}
