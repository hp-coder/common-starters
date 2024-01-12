package com.hp.joininmemory.context;

import com.hp.joininmemory.JoinFieldExecutor;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hp
 */
@Getter
@AllArgsConstructor
public class JoinFieldExecutorContext<DATA> {

    JoinFieldExecutor<DATA> executor;
}
