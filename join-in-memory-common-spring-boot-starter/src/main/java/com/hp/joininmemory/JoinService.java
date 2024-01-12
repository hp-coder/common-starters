package com.hp.joininmemory;

import cn.hutool.core.collection.CollUtil;

import java.util.Collections;
import java.util.List;

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
    default <T> void joinInMemory(List<T> t){
        if (CollUtil.isEmpty(t)){
            return;
        }
        if (t.size() == 1){
            joinInMemory(t.get(0));
        }
        joinInMemory((Class<T>) t.get(0).getClass(), t);
    }

    <T> void joinInMemory(Class<T> tCls, List<T> t);

    <T> void register(Class<T> clazz);
}
