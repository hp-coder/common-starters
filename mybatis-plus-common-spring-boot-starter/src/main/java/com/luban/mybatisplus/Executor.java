package com.luban.mybatisplus;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author HP
 * @date 2022/10/18
 */
public interface Executor<T> {

    Optional<T> execute();

    Executor<T> successHook(Consumer<T> consumer);

    Executor<T> errorHook(Consumer<? super  Throwable> consumer);
}
