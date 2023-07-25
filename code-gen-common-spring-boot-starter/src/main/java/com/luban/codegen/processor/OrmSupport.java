package com.luban.codegen.processor;

import com.luban.codegen.constant.Orm;

import java.util.Objects;

/**
 * @author hp
 */
public interface OrmSupport {
    default boolean supportedOrm(Orm orm) {
        return Objects.equals(orm, Orm.SPRING_DATA_JPA);
    }
}
