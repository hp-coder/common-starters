package com.hp.joininmemory.infrastructure;

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
        LOCAL_REPO.put(4L, new JoinUser(4L, "user4"));
        LOCAL_REPO.put(5L, new JoinUser(5L, "user5"));
        LOCAL_REPO.put(6L, new JoinUser(6L, "user6"));
    }

    public List<JoinUser> findAllById(Collection<Long> ids) {
        return findAllById(ids, StrUtil.EMPTY);
    }

    public List<JoinUser> findAllById(Collection<Long> ids, String param1) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        log.info("Querying, ids={}", CollUtil.join(ids, StrUtil.COMMA));
        return ids.stream()
                .map(id -> LOCAL_REPO.getOrDefault(id, null))
                .filter(Objects::nonNull)
                .map(i -> new JoinUser(i.id(), i.name() + param1))
                .collect(Collectors.toList());
    }
}
