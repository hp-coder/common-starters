package com.hp.spiderman.factory;

/**
 * @author hp
 */
public interface DomNodesExecutorFactory {
    <DATA> DomNodesExecutor<DATA> createFor(Class<DATA> clazz);
}
