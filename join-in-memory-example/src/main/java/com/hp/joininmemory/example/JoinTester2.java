package com.hp.joininmemory.example;

import com.hp.joininmemory.annotation.JoinInMemoryConfig;
import com.hp.joininmemory.constant.ExecuteLevel;
import com.hp.joininmemory.constant.JoinFieldProcessPolicy;
import com.hp.joininmemory.constant.JoinInMemoryExecutorType;
import lombok.Data;

/**
 * @author hp
 */
@Data
@JoinInMemoryConfig(
        executorType = JoinInMemoryExecutorType.PARALLEL,
        processPolicy = JoinFieldProcessPolicy.GROUPED
)
public class JoinTester2 {


    String createdBy;

    @JoinUsernameOnUserId(keyFromSourceData = "createdBy")
    String creator;

    String updatedBy;

    @JoinUsernameOnUserId(keyFromSourceData = "updatedBy")
    String updater;

    Long removedBy;

    @JoinUsernameOnUserId(keyFromSourceData = "removedBy", sourceDataKeyConverter = "", runLevel = ExecuteLevel.FOURTH)
    String remover;

    public JoinTester2(String createdBy, String updatedBy) {
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }
}
