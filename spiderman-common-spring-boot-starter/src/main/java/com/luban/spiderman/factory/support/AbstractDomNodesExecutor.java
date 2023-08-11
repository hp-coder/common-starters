package com.luban.spiderman.factory.support;

import com.google.common.base.Preconditions;
import com.luban.spiderman.factory.DomNodeExecutor;
import com.luban.spiderman.factory.DomNodesExecutor;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.Comparator;
import java.util.List;

/**
 * @author hp
 */
public abstract class AbstractDomNodesExecutor<DATA> implements DomNodesExecutor<DATA> {

    @Getter(AccessLevel.PROTECTED)
    private final Class<DATA> clazz;
    @Getter(AccessLevel.PROTECTED)
    private final List<DomNodeExecutor<DATA>> spiderNodeExecutors;


    public AbstractDomNodesExecutor(Class<DATA> clazz, List<DomNodeExecutor<DATA>> spiderNodeExecutors) {
        Preconditions.checkArgument(clazz != null);
        Preconditions.checkArgument(spiderNodeExecutors != null);
        this.clazz = clazz;
        this.spiderNodeExecutors = spiderNodeExecutors;
        this.spiderNodeExecutors.sort(Comparator.comparing(DomNodeExecutor::runOnLevel));
    }

}
