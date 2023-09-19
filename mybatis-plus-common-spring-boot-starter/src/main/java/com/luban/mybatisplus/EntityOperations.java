package com.luban.mybatisplus;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @author hp
 * @date 2022/10/18
 */
public abstract class EntityOperations {
    public static <T> EntityUpdater<T> doUpdate(BaseMapper<T> baseMapper){
        return new EntityUpdater<>(baseMapper);
    }

    public static <T> EntityCreator<T> doCreate(BaseMapper<T> baseMapper){
        return new EntityCreator<>(baseMapper);
    }
}
