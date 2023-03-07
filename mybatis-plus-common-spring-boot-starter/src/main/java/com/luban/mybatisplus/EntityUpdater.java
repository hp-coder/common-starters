package com.luban.mybatisplus;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.google.common.base.Preconditions;
import com.luban.mybatisplus.group.UpdateGroup;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author HP
 * @date 2022/10/18
 */
@Slf4j
public class EntityUpdater<T> extends BaseEntityOperation implements Loader<T>, UpdateHandler<T>, Executor<T> {

    private final BaseMapper<T> baseMapper;
    private T entity;
    private Consumer<T> successHook = _0 -> log.info("successfully updated");
    private Consumer<? super Throwable> errorHook = Throwable::printStackTrace;

    public EntityUpdater(BaseMapper<T> baseMapper) {
        this.baseMapper = baseMapper;
    }

    @Override
    public Optional<T> execute() {
        doValidate(this.entity, UpdateGroup.class);
        final T update = Try.of(() -> {
                    baseMapper.updateById(entity);
                    return this.entity;
                })
                .onSuccess(successHook)
                .onFailure(errorHook)
                .getOrNull();
        return Optional.ofNullable(update);
    }

    @Override
    public Executor<T> successHook(Consumer<T> consumer) {
        this.successHook = consumer;
        return this;
    }

    @Override
    public Executor<T> errorHook(Consumer<? super Throwable> consumer) {
        this.errorHook = consumer;
        return this;
    }

    @Override
    public UpdateHandler<T> loadById(Serializable id) {
        Assert.notNull(id, "Id Cannot Be Null");
        final T t = baseMapper.selectById(id);
        if (Objects.isNull(t)) {
            //TODO 自定义业务异常等
            throw new RuntimeException("Record Not Found");
        }
        this.entity = t;
        return this;
    }

    @Override
    public UpdateHandler<T> load(Supplier<T> t) {
        this.entity = t.get();
        return this;
    }

    @Override
    public Executor<T> update(Consumer<T> consumer) {
        Preconditions.checkArgument(Objects.nonNull(entity), "entity is null");
        consumer.accept(this.entity);
        return this;
    }
}
