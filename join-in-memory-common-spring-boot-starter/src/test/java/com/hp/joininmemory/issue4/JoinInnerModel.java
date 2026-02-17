package com.hp.joininmemory.issue4;

import cn.hutool.core.collection.CollUtil;
import com.google.common.base.Preconditions;
import com.hp.joininmemory.annotation.AfterJoin;
import com.hp.joininmemory.annotation.JoinInMemoryConfig;
import com.hp.joininmemory.constant.ExecuteLevel;
import com.hp.joininmemory.constant.JoinFieldProcessPolicy;
import com.hp.joininmemory.constant.JoinInMemoryExecutorType;
import com.hp.joininmemory.issue4.annotation.JoinInnerLevel5ModelOnInnerModeId;
import com.hp.joininmemory.issue4.annotation.JoinInnerLevel6ModelOnInnerModeId;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author hp
 */

@JoinInMemoryConfig(
        fieldProcessPolicy = JoinFieldProcessPolicy.GROUPED,
        executorType = JoinInMemoryExecutorType.PARALLEL
)
@Data
@Slf4j
public class JoinInnerModel {

    private Long id;

    private Long outerId;

    @JoinInnerLevel5ModelOnInnerModeId(keyFromSourceData = "id", runLevel = ExecuteLevel.FIFTH)
    private JoinInnerLevel5Model level5Model;

    @JoinInnerLevel6ModelOnInnerModeId(keyFromSourceData = "level5Model.id", runLevel = ExecuteLevel.SIXTH)
    private List<JoinInnerLevel6Model> level6Models;

    public JoinInnerModel() {
    }

    public JoinInnerModel(Long id) {
        this.id = id;
        this.outerId = 1L;
    }

    @AfterJoin
    public void afterJoin() {
        log.warn("inner after join called, {}" , Thread.currentThread().getName());
        Preconditions.checkArgument(CollUtil.isNotEmpty(level6Models));
    }
}

