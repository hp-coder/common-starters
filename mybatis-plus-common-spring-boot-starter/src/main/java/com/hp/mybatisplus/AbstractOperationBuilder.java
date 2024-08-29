package com.hp.mybatisplus;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.hp.common.base.enums.BaseEnum;
import com.hp.common.base.function.TerFunction;
import com.hp.common.base.utils.ProgrammaticHelper;
import com.hp.common.base.valueobject.SingleValueObject;
import com.hp.mybatisplus.converter.TypeHandlerAdapter;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
abstract class AbstractOperationBuilder<AGG, BUILDER extends AbstractOperationBuilder<AGG, BUILDER, WRAPPER>, WRAPPER extends AbstractWrapper<AGG, String, WRAPPER>> {

    @SuppressWarnings("unchecked")
    protected final BUILDER self = (BUILDER) this;

    protected final WRAPPER wrapper;

    protected abstract BUILDER instance(WRAPPER wrapper);

    protected AbstractOperationBuilder(WRAPPER wrapper) {
        this.wrapper = wrapper;
    }

    protected void action(SFunction<AGG, ?> queryField, Function<String, WRAPPER> action) {
        action(TableHelper.columnName(Objects.requireNonNull(queryField)), action);
    }

    protected void action(String queryField, Function<String, WRAPPER> action) {
        Objects.requireNonNull(action).apply(Objects.requireNonNull(queryField));
    }

    protected <VALUE, CONVERTED_VALUE> void action(SFunction<AGG, ?> queryField, VALUE value, BiFunction<String, CONVERTED_VALUE, WRAPPER> action) {
        this.action(TableHelper.columnName(Objects.requireNonNull(queryField)), convert(queryField, value), action);
    }

    protected <VALUE, CONVERTED_VALUE> void action(String queryField, VALUE value, BiFunction<String, CONVERTED_VALUE, WRAPPER> action) {
        if (validateParameter().test(value)) {
            Objects.requireNonNull(action).apply(queryField, convert(value));
        }
    }

    protected <VALUE, CONVERTED_VALUE> void action(SFunction<AGG, ?> queryField, VALUE value1, VALUE value2, TerFunction<String, CONVERTED_VALUE, CONVERTED_VALUE, WRAPPER> action) {
        this.action(TableHelper.columnName(queryField), convert(queryField, value1), convert(queryField, value2), action);
    }

    protected <VALUE, CONVERTED_VALUE> void action(String queryField, VALUE value1, VALUE value2, TerFunction<String, CONVERTED_VALUE, CONVERTED_VALUE, WRAPPER> action) {
        final Predicate<Object> valuePredicate = validateParameter();
        if (validateParameter().test(value1) && valuePredicate.test(value2)) {
            Objects.requireNonNull(action).apply(queryField, convert(value1), convert(value2));
        }
    }

    @SuppressWarnings("unchecked")
    protected <VALUE, CONVERTED_VALUE, FIELD, COLUMN> CONVERTED_VALUE convert(@Nonnull SFunction<AGG, ?> queryField, @Nonnull VALUE value) {
        try {
            final Optional<TypeHandlerAdapter<FIELD, COLUMN>> typeHandlerCodeGenAdapter = TableHelper.columnConverter(queryField);
            if (typeHandlerCodeGenAdapter.isPresent()) {
                if (value instanceof Collection<?> collection) {
                    return (CONVERTED_VALUE) collection.stream().filter(Objects::nonNull).map(i -> ((COLUMN) typeHandlerCodeGenAdapter.get().fieldToColumn((FIELD) i))).toList();
                } else {
                    return ((CONVERTED_VALUE) typeHandlerCodeGenAdapter.get().fieldToColumn(((FIELD) value)));
                }
            }
            return (CONVERTED_VALUE) value;
        } catch (Exception e) {
            log.error("Fail at converting parameter={} with the TypeHandler annotated on the column={}, fallback to the default conversion logic.", value, TableHelper.columnName(queryField), e);
            return (CONVERTED_VALUE) value;
        }
    }

    @SuppressWarnings("unchecked")
    protected <VALUE, CONVERTED_VALUE> CONVERTED_VALUE convert(@Nonnull VALUE value) {
        if (value instanceof BaseEnum<?, ?> baseEnum) {
            return (CONVERTED_VALUE) baseEnum.getCode();
        }
        if (value instanceof SingleValueObject<?> singleValueObject) {
            return ((CONVERTED_VALUE) singleValueObject.value());
        }
        if (value instanceof Collection<?> collection) {
            final Optional<?> first = collection.stream().filter(Objects::nonNull).findFirst();
            if (first.isPresent() && first.get() instanceof BaseEnum<?, ?>) {
                final Collection<BaseEnum<?, ?>> baseEnums = (Collection<BaseEnum<?, ?>>) collection;
                return (CONVERTED_VALUE) baseEnums.stream().filter(Objects::nonNull).map(BaseEnum::getCode).distinct().toList();
            } else if (first.isPresent() && first.get() instanceof SingleValueObject<?>) {
                final Collection<SingleValueObject<?>> singleValueObjects = (Collection<SingleValueObject<?>>) collection;
                return (CONVERTED_VALUE) singleValueObjects.stream().filter(Objects::nonNull).map(SingleValueObject::value).distinct().toList();
            } else {
                return (CONVERTED_VALUE) collection.stream().filter(Objects::nonNull).collect(Collectors.toList());
            }
        }
        return ((CONVERTED_VALUE) value);
    }

    protected Predicate<Object> validateParameter() {
        return value -> {
            if (Objects.isNull(value)) {
                return false;
            }
            return switch (value) {
                case String sValue -> StrUtil.isNotEmpty(sValue);
                case Map<?, ?> mValue -> MapUtil.isNotEmpty(mValue);
                case Collection<?> cValue -> CollUtil.isNotEmpty(cValue);
                default -> true;
            };
        };
    }

    public BUILDER and(Consumer<BUILDER> consumer) {
        this.wrapper.and(wrapper -> consumer.accept(instance(wrapper)));
        return self;
    }

    public BUILDER or(Consumer<BUILDER> consumer) {
        this.wrapper.or(wrapper -> consumer.accept(instance(wrapper)));
        return self;
    }

    public BUILDER isNull(SFunction<AGG, ?> queryField) {
        action(queryField, wrapper::isNull);
        return self;
    }

    public BUILDER isNotNull(SFunction<AGG, ?> queryField) {
        action(queryField, wrapper::isNotNull);
        return self;
    }

    public <VALUE> BUILDER eq(SFunction<AGG, ?> queryField, VALUE value) {
        action(queryField, value, wrapper::eq);
        return self;
    }

    @SuppressWarnings("unchecked")
    public <VALUE> BUILDER in(SFunction<AGG, ?> queryField, Collection<VALUE> value) {
        action(queryField, value, (fun, cv) -> wrapper.in(fun, ((Collection<VALUE>) cv)));
        return self;
    }

    @SafeVarargs
    public final <VALUE> BUILDER in(SFunction<AGG, ?> queryField, VALUE value, VALUE... values) {
        return in(queryField, ProgrammaticHelper.getSafeVarargs(value, values));
    }

    public <VALUE> BUILDER in(SFunction<AGG, ?> queryField, VALUE value) {
        action(queryField, value, wrapper::in);
        return self;
    }

    @SuppressWarnings("unchecked")
    public <VALUE> BUILDER notIn(SFunction<AGG, ?> queryField, Collection<VALUE> value) {
        action(queryField, value, (fun, cv) -> wrapper.notIn(fun, ((Collection<VALUE>) cv)));
        return self;
    }

    @SafeVarargs
    public final <VALUE> BUILDER notIn(SFunction<AGG, ?> queryField, VALUE value, VALUE... values) {
        return notIn(queryField, ProgrammaticHelper.getSafeVarargs(value, values));
    }

    public <VALUE> BUILDER notIn(SFunction<AGG, ?> queryField, VALUE value) {
        action(queryField, value, wrapper::notIn);
        return self;
    }

    public <VALUE> BUILDER like(SFunction<AGG, ?> queryField, VALUE value) {
        action(queryField, value, wrapper::like);
        return self;
    }

    public <VALUE> BUILDER likeRight(SFunction<AGG, ?> queryField, VALUE value) {
        action(queryField, value, wrapper::likeRight);
        return self;
    }

    public <VALUE> BUILDER likeLeft(SFunction<AGG, ?> queryField, VALUE value) {
        action(queryField, value, wrapper::likeLeft);
        return self;
    }

    public <VALUE> BUILDER le(SFunction<AGG, ?> queryField, VALUE value) {
        action(queryField, value, wrapper::le);
        return self;
    }

    public <VALUE> BUILDER lt(SFunction<AGG, ?> queryField, VALUE value) {
        action(queryField, value, wrapper::lt);
        return self;
    }

    public <VALUE> BUILDER ge(SFunction<AGG, ?> queryField, VALUE value) {
        action(queryField, value, wrapper::ge);
        return self;
    }

    public <VALUE> BUILDER gt(SFunction<AGG, ?> queryField, VALUE value) {
        action(queryField, value, wrapper::gt);
        return self;
    }

    public <VALUE> BUILDER between(SFunction<AGG, ?> queryField, VALUE lowerBoundary, VALUE upperBoundary) {
        action(queryField, lowerBoundary, upperBoundary, wrapper::between);
        return self;
    }

    @SafeVarargs
    public final <VALUE> BUILDER having(String havingSql, VALUE queryParam, VALUE... queryParams) {
        action(havingSql, ProgrammaticHelper.getSafeVarargs(queryParam, queryParams), wrapper::having);
        return self;
    }

    @SafeVarargs
    public final BUILDER groupBy(SFunction<AGG, ?> field, SFunction<AGG, ?>... fields) {
        wrapper.groupBy(ProgrammaticHelper.getSafeVarargs(field, fields).stream().map(TableHelper::columnName).toList());
        return self;
    }

    @SafeVarargs
    public final BUILDER orderByAsc(SFunction<AGG, ?> field, SFunction<AGG, ?>... fields) {
        wrapper.orderByAsc(ProgrammaticHelper.getSafeVarargs(field, fields).stream().map(TableHelper::columnName).toList());
        return self;
    }

    @SafeVarargs
    public final BUILDER orderByDesc(SFunction<AGG, ?> field, SFunction<AGG, ?>... fields) {
        wrapper.orderByDesc(ProgrammaticHelper.getSafeVarargs(field, fields).stream().map(TableHelper::columnName).toList());
        return self;
    }

    public BUILDER last(String lastSql) {
        if (StrUtil.isEmpty(lastSql)) {
            return self;
        }
        wrapper.last(lastSql);
        return self;
    }

    public BUILDER limit(int limit) {
        return last("limit %s".formatted(limit));
    }

    public BUILDER limit(int index, int size) {
        return last("limit %s, %s".formatted(index, size));
    }

    public WRAPPER build() {
        return wrapper;
    }
}
