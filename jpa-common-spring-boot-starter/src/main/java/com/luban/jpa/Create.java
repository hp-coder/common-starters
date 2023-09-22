package com.luban.jpa;

import java.util.function.Supplier;

/**
 * @author hp
 */
public interface Create<T> {

  UpdateHandler<T> create(Supplier<T> supplier);

}
