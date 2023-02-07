package com.luban.mybatisplus;

import java.util.function.Consumer;

/**
 * @author HP
 * @date 2022/10/18
 */
public interface UpdateHandler<T> {

    Executor<T> update(Consumer<T> consumer);
}
