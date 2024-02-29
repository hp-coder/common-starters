package com.hp.codegen.processor;

import com.hp.codegen.constant.Orm;

import java.util.Arrays;

/**
 * @author hp
 */
public interface OrmSupport {

    default boolean supportedOrm(Orm orm) {
        return Arrays.asList(Orm.values()).contains(orm);
    }
}
