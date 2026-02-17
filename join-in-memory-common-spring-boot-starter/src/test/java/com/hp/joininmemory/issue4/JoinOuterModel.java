package com.hp.joininmemory.issue4;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.google.common.base.Preconditions;
import com.hp.joininmemory.JoinService;
import com.hp.joininmemory.annotation.AfterJoin;
import com.hp.joininmemory.annotation.JoinInMemoryConfig;
import com.hp.joininmemory.constant.JoinFieldProcessPolicy;
import com.hp.joininmemory.constant.JoinInMemoryExecutorType;
import com.hp.joininmemory.issue4.annotation.JoinInnerModelOnId;
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
public class JoinOuterModel {

    private Long id = 1L;

    @JoinInnerModelOnId(keyFromSourceData = "id")
    public List<JoinInnerModel> innerModels;

    @AfterJoin
    public void afterJoin() {
        log.info("out after join called, {}, innerModels size = {}", Thread.currentThread().getName(), innerModels.size());
        Preconditions.checkArgument(CollUtil.isNotEmpty(innerModels));
        SpringUtil.getBean(JoinService.class).joinInMemory(innerModels);
    }
}
