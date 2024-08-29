package com.hp.lazyloader.factory;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import com.hp.common.base.utils.SpELHelper;
import com.hp.lazyloader.PropertyLazyLoader;
import com.hp.lazyloader.anntation.LazyLoader;
import org.springframework.core.annotation.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author hp
 */
public class LazyLoaderFactory {

    private final SpELHelper spELHelper;

    public LazyLoaderFactory(SpELHelper spELHelper) {
        this.spELHelper = Objects.requireNonNull(spELHelper);
    }

    public List<PropertyLazyLoader> createFor(Class<?> clazz) {
        return Arrays.stream(ClassUtil.getDeclaredFields(clazz))
                .map(field -> createPropertyLazyLoader(clazz, field))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private PropertyLazyLoader createPropertyLazyLoader(Class<?> clazz, Field field) {
        LazyLoader lazyLoader = AnnotatedElementUtils.findMergedAnnotation(field, LazyLoader.class);
        if (Objects.isNull(lazyLoader)) {
            final Method getMethod = ReflectUtil.getMethodByNameIgnoreCase(clazz, "get%s" + field.getName());
            if (Objects.isNull(getMethod)) {
                return null;
            } else {
                lazyLoader = AnnotatedElementUtils.findMergedAnnotation(getMethod, LazyLoader.class);
            }
        }
        if (Objects.isNull(lazyLoader)) {
            return null;
        }
        String replaceableSpEL = lazyLoader.value();
        // Reduce the possibility of different annotations being grouped.
        final MergedAnnotations from = MergedAnnotations.from(field, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY, RepeatableContainers.none());
        // The real annotation that was used to annotate the field.
        final MergedAnnotation<?> rootAnnotation = from.get(LazyLoader.class).getRoot();
        final AnnotationAttributes annotationAttributes = rootAnnotation.asAnnotationAttributes();
        // Using annotation attributes as variables to process SpEL
        for (Map.Entry<String, Object> entry : annotationAttributes.entrySet()) {
            String key = "#%s".formatted(entry.getKey());
            String value = String.valueOf(entry.getValue());
            replaceableSpEL = replaceableSpEL.replace(key, value);
        }
        return PropertyLazyLoader.create(field, spELHelper.newGetterInstance(replaceableSpEL));
    }

}
