package com.hp.joininmemory.feature_join_at_return_dynamic_join_fields;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026/1/22
 */
@Repository
public class DynamicFieldsUserRepository {

    public List<User> findByIds(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) return Collections.emptyList();

        return ids.stream()
                .map(id -> new User(id, "User-" + id))
                .toList();
    }
}
