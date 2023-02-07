package com.luban.jpa;

import java.util.function.Supplier;

/**
 * @author gim 2022/1/28 9:55 下午
 */
public interface Create<T> {

  UpdateHandler<T> create(Supplier<T> supplier);

}
