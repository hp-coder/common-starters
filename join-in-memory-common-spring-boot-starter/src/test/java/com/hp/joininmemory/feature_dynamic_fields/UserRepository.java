package com.hp.joininmemory.feature_dynamic_fields;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Slf4j
@Repository
public class UserRepository {

    public List<User> findByUserId(Collection<Long> userIds) {
        log.info("查询参数大小={}", userIds.size());
        return userIds.stream()
                .map(User::new)
                .toList();
    }
}
