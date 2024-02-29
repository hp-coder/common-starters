package com.hp.mybatisplus;

import java.util.function.Supplier;

/**
 * @author hp
 * @date 2022/10/18
 */
public interface Create<T> {

    UpdateHandler<T> create(Supplier<T> supplier);
}
