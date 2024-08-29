package com.hp.jpa.converter;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.hp.common.base.enums.BaseEnum;
import com.hp.common.base.enums.BaseEnumBasedAdapter;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author hp
 */
@Converter
public abstract class AbstractBaseEnumTypesConverter<T extends Enum<T> & BaseEnum<T, E>, E, FIELD extends Collection<T>> implements AttributeConverter<Collection<T>, String>, BaseEnumBasedAdapter<T, E> {
    protected final Class<T> baseEnumType = Objects.requireNonNull(getBaseEnumType());
    protected final Class<FIELD> fieldType = Objects.requireNonNull(getFieldType());

    protected final String separator;

    public AbstractBaseEnumTypesConverter(String separator) {
        this.separator = separator;
    }

    @SuppressWarnings("unchecked")
    protected Class<FIELD> getFieldType() {
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType parameterizedType) {
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            return (Class<FIELD>) ((ParameterizedType) actualTypeArguments[actualTypeArguments.length - 1]).getRawType();
        }
        return null;
    }

    @Override
    public String convertToDatabaseColumn(Collection<T> attribute) {
        return Optional.ofNullable(attribute)
                .map(e -> e.stream()
                        .filter(Objects::nonNull)
                        .map(BaseEnum::getCode)
                        .map(String::valueOf)
                        .collect(Collectors.joining(this.separator)))
                .orElse(null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<T> convertToEntityAttribute(String dbData) {
        return (Collection<T>) Optional.ofNullable(dbData)
                .map(s -> {
                    if (StrUtil.isEmpty(s)) {
                        return Collections.emptyList();
                    } else {
                        return Arrays.stream(s.split(this.separator))
                                .filter(StrUtil::isNotEmpty)
                                .map(code -> BaseEnum.parseByCode(baseEnumType, convert(code)))
                                .collect(Collectors.toCollection(() -> CollUtil.create(fieldType, baseEnumType)));
                    }
                }).orElse(Collections.emptyList());
    }

    @SuppressWarnings("unchecked")
    protected E convert(String code) {
        return (E) code;
    }
}
