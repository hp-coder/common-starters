package com.luban.mybatisplus;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.io.Serializable;
import java.util.*;

/**
 * @author hp
 */
@Mapper
public interface AbstractBaseMapper<AGG_ROOT, ID extends Serializable> extends BaseMapper<AGG_ROOT> {

    default Optional<AGG_ROOT> findById(ID id) {
        if (Objects.isNull(id)) {
            return Optional.empty();
        }
        return Optional.ofNullable(selectById(id));
    }


    default List<AGG_ROOT> findAllById(Collection<ID> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        final List<AGG_ROOT> aggRoots = selectBatchIds(ids);

        if (CollUtil.isEmpty(aggRoots)) {
            return Collections.emptyList();
        }
        return aggRoots;
    }
}
