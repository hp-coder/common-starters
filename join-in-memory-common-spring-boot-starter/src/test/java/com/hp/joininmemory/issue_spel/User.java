package com.hp.joininmemory.issue_spel;

import com.hp.joininmemory.annotation.JoinInMemoryConfig;
import com.hp.joininmemory.constant.JoinInMemoryExecutorType;
import lombok.Data;

/**
 * @author hp
 */
@Data
@JoinInMemoryConfig(
        executorType = JoinInMemoryExecutorType.PARALLEL
)
public class User {

    private Long id;

    @JoinNameOnId("id")
    private String name;
}
