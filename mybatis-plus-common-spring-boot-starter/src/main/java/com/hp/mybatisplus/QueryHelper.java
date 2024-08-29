package com.hp.mybatisplus;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.google.common.collect.Lists;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

/**
 * @author hp
 */
@Slf4j
@UtilityClass
public class QueryHelper {

    public static <AGG> QueryBuilder<AGG> builder() {
        return new QueryBuilder<>();
    }

    private abstract class AbstractQueryBuilder<AGG, BUILDER extends AbstractQueryBuilder<AGG, BUILDER>> extends AbstractOperationBuilder<AGG, BUILDER, QueryWrapper<AGG>> {
        private AbstractQueryBuilder() {
            this(new QueryWrapper<>());
        }

        private AbstractQueryBuilder(QueryWrapper<AGG> queryWrapper) {
            super(queryWrapper);
        }

        @SafeVarargs
        public final BUILDER select(SFunction<AGG, ?> field, SFunction<AGG, ?>... fields) {
            final List<SFunction<AGG, ?>> sFunctions = Lists.newArrayList(field);
            Optional.ofNullable(fields).ifPresent(arr -> sFunctions.addAll(Lists.newArrayList(arr)));
            wrapper.select(sFunctions.stream().map(TableHelper::columnName).toList());
            return self;
        }
    }

    public static class QueryBuilder<AGG> extends AbstractQueryBuilder<AGG, QueryBuilder<AGG>> {

        public QueryBuilder() {
        }

        private QueryBuilder(QueryWrapper<AGG> queryWrapper) {
            super(queryWrapper);
        }

        @Override
        protected QueryBuilder<AGG> instance(QueryWrapper<AGG> queryWrapper) {
            return new QueryBuilder<>(queryWrapper);
        }
    }
}
