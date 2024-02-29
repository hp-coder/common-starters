package com.hp.excel.handler;

import java.util.function.Function;

/**
 * @author hp
 * @date 2022/11/7
 */
public interface ColumnDynamicSelectDataHandler<T, R> {

    Function<T, R> source();
}
