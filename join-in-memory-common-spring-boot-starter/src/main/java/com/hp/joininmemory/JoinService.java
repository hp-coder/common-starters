package com.hp.joininmemory;

import cn.hutool.core.collection.CollUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * @author hp 2023/3/27
 */
public interface JoinService {

    @SuppressWarnings("unchecked")
    default <T> void joinInMemory(T t){
        if (t == null){
            return;
        }
        joinInMemory((Class<T>) t.getClass(), Collections.singletonList(t));
    }

    @SuppressWarnings("unchecked")
    default <T> void joinInMemory(Collection<T> t){
        if (CollUtil.isEmpty(t)){
            return;
        }
        final Iterator<T> iterator = t.iterator();
        final T next = iterator.next();
        if (t.size() == 1){
            joinInMemory(next);
        }
        joinInMemory((Class<T>) next.getClass(), t);
    }

    <T> void joinInMemory(Class<T> tCls, Collection<T> t);

    <T> void register(Class<T> clazz);
}
