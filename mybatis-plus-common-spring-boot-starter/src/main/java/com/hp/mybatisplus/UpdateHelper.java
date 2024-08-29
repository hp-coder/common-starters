package com.hp.mybatisplus;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * @author hp
 */
@Slf4j
@UtilityClass
public class UpdateHelper {

    public static <AGG> UpdateHelper.UpdateBuilder<AGG> builder() {
        return new UpdateHelper.UpdateBuilder<>();
    }

    private abstract class AbstractUpdateBuilder<AGG, BUILDER extends AbstractUpdateBuilder<AGG, BUILDER>> extends AbstractOperationBuilder<AGG, BUILDER, UpdateWrapper<AGG>> {
        private AbstractUpdateBuilder() {
            this(new UpdateWrapper<>());
        }

        private AbstractUpdateBuilder(UpdateWrapper<AGG> updateWrapper) {
            super(updateWrapper);
        }

        public <VALUE> BUILDER set(SFunction<AGG, ?> field, VALUE value) {
            action(field, value, wrapper::set);
            return self;
        }
    }

    public static class UpdateBuilder<AGG> extends AbstractUpdateBuilder<AGG, UpdateBuilder<AGG>> {

        public UpdateBuilder() {
        }

        private UpdateBuilder(UpdateWrapper<AGG> updateWrapper) {
            super(updateWrapper);
        }

        @Override
        protected UpdateBuilder<AGG> instance(UpdateWrapper<AGG> wrapper) {
            return new UpdateBuilder<>(wrapper);
        }
    }
}
