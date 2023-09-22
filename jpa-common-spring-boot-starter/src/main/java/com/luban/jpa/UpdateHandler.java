package com.luban.jpa;

import java.util.function.Consumer;

/**
 * @author hp
 */
public interface UpdateHandler<T>{

  Executor<T> update(Consumer<T> consumer);

}
