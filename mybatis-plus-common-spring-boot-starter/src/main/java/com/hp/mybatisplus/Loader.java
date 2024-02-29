package com.hp.mybatisplus;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * @author hp
 * @date 2022/10/18
 */
public interface Loader<T> {

    UpdateHandler<T> loadById(Serializable id);

    UpdateHandler<T> load(Supplier<T> t);
}
