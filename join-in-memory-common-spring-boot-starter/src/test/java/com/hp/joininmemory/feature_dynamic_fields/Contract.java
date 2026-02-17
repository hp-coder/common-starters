package com.hp.joininmemory.feature_dynamic_fields;

import com.hp.joininmemory.annotation.JoinInMemoryConfig;
import com.hp.joininmemory.constant.JoinFieldProcessPolicy;
import com.hp.joininmemory.constant.JoinInMemoryExecutorType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@JoinInMemoryConfig(
        executorType = JoinInMemoryExecutorType.PARALLEL,
        fieldProcessPolicy = JoinFieldProcessPolicy.GROUPED
)
@Data
public class Contract {

    private Long createdBy;

    @JoinUsernameOnUserId("createdBy")
    private String creator;

    public void setCreator(String creator) {
        log.info("setting creator {}", creator);
        this.creator = creator;
    }

    private Long modifiedBy;

    @JoinUsernameOnUserId("modifiedBy")
    private String modifier;

    public void setModifier(String modifier) {
        log.info("setting modifier {}", modifier);
        this.modifier = modifier;
    }

    public Contract(Long createdBy, Long modifiedBy) {
        this.createdBy = createdBy;
        this.modifiedBy = modifiedBy;

    }
}
