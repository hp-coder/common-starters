package com.hp.joininmemory.example;

import com.hp.joininmemory.annotation.AfterJoin;
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
public class AfterJoinTester {


    String createdBy;

    @JoinUsernameOnUserId(keyFromSourceData = "#{createdBy}")
    String creator;

    String updatedBy;

    @JoinUsernameOnUserId(keyFromSourceData = "#{updatedBy}")
    String updater;

    Long removedBy;

    @JoinUsernameOnUserId(keyFromSourceData = "#{removedBy}", sourceDataKeyConverter = "")
    String remover;

    public AfterJoinTester(String createdBy, String updatedBy, Long removedBy) {
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.removedBy = removedBy;
    }

    @AfterJoin(runLevel = ExecuteLevel.TENTH)
    public void afterJoin(){
        System.out.println("AfterJoin: sequence = " + createdBy + updatedBy + removedBy);
    }

    @AfterJoin(runLevel = ExecuteLevel.FIRST)
    public void afterJoin2(){
        System.out.printf("AfterJoin2: createdBy=%s || updatedBy=%s || removedBy=%s%n",  creator  , updater , remover);
    }

    @AfterJoin(runLevel = ExecuteLevel.SECOND)
    public void afterJoin3(){
        System.out.println("AfterJoin3: The function is executed before the afterJoin() and the afterJoin2()");
    }
}
