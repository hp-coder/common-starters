package com.hp.joininmemory.example;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author hp
 */

@Service
@Slf4j
public class JoinRepository {

    public static final Map<Long, JoinUser> LOCAL_REPO;

    static {
        LOCAL_REPO = Maps.newHashMap();
        LOCAL_REPO.put(1L, new JoinUser(1L, "user1"));
        LOCAL_REPO.put(2L, new JoinUser(2L, "user2"));
        LOCAL_REPO.put(3L, new JoinUser(3L, "user3"));
    }

    public List<JoinUser> findAllById(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        final List<Long> safeIds = ids.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        log.info("Querying, ids={}", CollUtil.join(safeIds, StrUtil.COMMA));
        return safeIds.stream()
//                // Apologize for my laziness.
//                .peek(id -> {
//                    if (Objects.equals(2L, id)) {
//                        throw new IllegalArgumentException("join 发生 参数错误");
//                    }
//                })
                .map(id -> LOCAL_REPO.getOrDefault(id, null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
