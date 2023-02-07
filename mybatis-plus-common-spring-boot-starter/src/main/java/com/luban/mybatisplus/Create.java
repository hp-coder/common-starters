package com.luban.mybatisplus;

import java.util.function.Supplier;

/**
 * @author HP
 * @date 2022/10/18
 */
public interface Create<T> {

    UpdateHandler<T> create(Supplier<T> supplier);
}
