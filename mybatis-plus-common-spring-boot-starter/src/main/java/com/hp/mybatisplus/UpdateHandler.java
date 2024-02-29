package com.hp.mybatisplus;

import java.util.function.Consumer;

/**
 * @author hp
 * @date 2022/10/18
 */
public interface UpdateHandler<T> {

    Executor<T> update(Consumer<T> consumer);
}
