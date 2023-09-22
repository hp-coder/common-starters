package com.luban.jpa;

import java.util.function.Supplier;

/**
 * @author hp
 */
public interface Loader<T,ID> {

  UpdateHandler<T> loadById(ID id);

  UpdateHandler<T> load(Supplier<T> t);

}
