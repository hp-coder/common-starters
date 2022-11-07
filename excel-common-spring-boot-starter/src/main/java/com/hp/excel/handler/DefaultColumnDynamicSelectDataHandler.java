package com.hp.excel.handler;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * @author HP
 * @date 2022/11/7
 */
public class DefaultColumnDynamicSelectDataHandler implements ColumnDynamicSelectDataHandler<String, List<String>> {

    @Override
    public Function<String, List<String>> source() {
        return t -> Collections.emptyList();
    }
}
