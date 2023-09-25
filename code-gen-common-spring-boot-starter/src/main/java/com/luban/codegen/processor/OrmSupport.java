package com.luban.codegen.processor;

import com.luban.codegen.constant.Orm;

import java.util.Arrays;

/**
 * @author hp
 */
public interface OrmSupport {

    default boolean supportedOrm(Orm orm) {
        return Arrays.asList(Orm.values()).contains(orm);
    }
}
