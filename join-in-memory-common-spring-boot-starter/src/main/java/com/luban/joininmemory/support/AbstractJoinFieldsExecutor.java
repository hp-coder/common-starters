package com.luban.joininmemory.support;

import com.google.common.base.Preconditions;
import com.luban.joininmemory.AfterJoinMethodExecutor;
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
    @Getter(AccessLevel.PROTECTED)
    private final List<AfterJoinMethodExecutor<DATA>> afterJoinMethodExecutors;


    public AbstractJoinFieldsExecutor(Class<DATA> clazz,
                                      List<JoinFieldExecutor<DATA>> joinFieldExecutors,
                                      List<AfterJoinMethodExecutor<DATA>> afterJoinMethodExecutors
    ) {
        Preconditions.checkArgument(clazz != null);
        Preconditions.checkArgument(joinFieldExecutors != null);
        Preconditions.checkArgument(afterJoinMethodExecutors != null);
        this.clazz = clazz;
        this.joinFieldExecutors = joinFieldExecutors;
        this.joinFieldExecutors.sort(Comparator.comparing(JoinFieldExecutor::runOnLevel));
        this.afterJoinMethodExecutors = afterJoinMethodExecutors;
        this.afterJoinMethodExecutors.sort(Comparator.comparing(AfterJoinMethodExecutor::runOnLevel));
    }
}
