package com.luban.joininmemory.support;

import com.google.common.base.Preconditions;
import com.luban.joininmemory.JoinFieldExecutor;
import com.luban.joininmemory.JoinFieldsExecutor;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.Comparator;
import java.util.List;

/**
 * @author hp 2023/3/27
 */
abstract class AbstractJoinFieldsExecutor<DATA> implements JoinFieldsExecutor<DATA> {

    @Getter(AccessLevel.PROTECTED)
    private final Class<DATA> clazz;
    @Getter(AccessLevel.PROTECTED)
    private final List<JoinFieldExecutor<DATA>> joinFieldExecutors;


    public AbstractJoinFieldsExecutor(Class<DATA> clazz, List<JoinFieldExecutor<DATA>> joinFieldExecutors) {
        Preconditions.checkArgument(clazz != null);
        Preconditions.checkArgument(joinFieldExecutors != null);
        this.clazz = clazz;
        this.joinFieldExecutors = joinFieldExecutors;
        this.joinFieldExecutors.sort(Comparator.comparing(JoinFieldExecutor::runOnLevel));
    }
}
