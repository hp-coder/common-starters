package com.luban.mybatisplus;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.luban.mybatisplus.group.CreateGroup;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author HP
 * @date 2022/10/18
 */
@Slf4j
public class EntityCreator<T> extends BaseEntityOperation implements Create<T>, UpdateHandler<T>, Executor<T> {

    private final BaseMapper<T> baseMapper;
    private T t;
    private Consumer<T> successHook = _0 -> log.info("Successfully Saved");
    private Consumer<? super Throwable> errorHook = _0 -> log.info("Successfully Saved");

    public EntityCreator(BaseMapper<T> baseMapper) {
        this.baseMapper = baseMapper;
    }

    @Override
    public UpdateHandler<T> create(Supplier<T> supplier) {
        this.t = supplier.get();
        return this;
    }

    @Override
    public Optional<T> execute() {
        doValidate(t, CreateGroup.class);
        T save = Try.of(() -> {
                    baseMapper.insert(t);
                    return this.t;
                })
                .onSuccess(successHook)
                .onFailure(errorHook)
                .getOrNull();
        return Optional.ofNullable(save);
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
    public Executor<T> update(Consumer<T> consumer) {
        Assert.notNull(t, "Entity cannot be null");
        consumer.accept(this.t);
        return this;
    }
}
