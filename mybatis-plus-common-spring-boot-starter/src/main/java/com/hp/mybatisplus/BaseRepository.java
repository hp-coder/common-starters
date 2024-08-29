package com.hp.mybatisplus;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.io.Serializable;
import java.util.*;

/**
 * @author hp
 */
public interface BaseRepository<AGGREGATE_ROOT, ID extends Serializable> extends BaseMapper<AGGREGATE_ROOT> {

    default Optional<AGGREGATE_ROOT> findById(ID id) {
        if (Objects.isNull(id)) {
            return Optional.empty();
        }
        return Optional.ofNullable(selectById(id));
    }

    default List<AGGREGATE_ROOT> findAllById(Collection<ID> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        final List<AGGREGATE_ROOT> aggRoots = selectBatchIds(ids);

        if (CollUtil.isEmpty(aggRoots)) {
            return Collections.emptyList();
        }
        return aggRoots;
    }
}
