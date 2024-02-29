package com.hp.jpa;

import java.util.function.Supplier;

/**
 * @author hp
 */
public interface Create<T> {

  UpdateHandler<T> create(Supplier<T> supplier);

}
