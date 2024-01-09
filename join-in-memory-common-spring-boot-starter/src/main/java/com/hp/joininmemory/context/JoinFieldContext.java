package com.hp.joininmemory.context;

import com.hp.joininmemory.support.AbstractJoinFieldV2Executor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

/**
 * @author hp
 */
@Getter
@Setter
public class JoinFieldContext<SOURCE_DATA, SOURCE_JOIN_KEY, JOIN_KEY, JOIN_DATA, DATA_JOIN_KEY, JOIN_RESULT> {

    private final AbstractJoinFieldV2Executor<SOURCE_DATA, SOURCE_JOIN_KEY, JOIN_KEY, JOIN_DATA, DATA_JOIN_KEY, JOIN_RESULT> executor;

    private SOURCE_DATA sourceData;

    private SOURCE_JOIN_KEY sourceJoinKey;

    private JOIN_KEY joinKey;

    private List<JOIN_DATA> joinData;

    private DATA_JOIN_KEY dataJoinKey;

    private JOIN_RESULT joinResult;

    public JoinFieldContext(
            AbstractJoinFieldV2Executor<SOURCE_DATA, SOURCE_JOIN_KEY, JOIN_KEY, JOIN_DATA, DATA_JOIN_KEY, JOIN_RESULT> executor,
            SOURCE_DATA sourceData
    ) {
        this.sourceData = sourceData;
        this.executor = executor;
    }

    public boolean notEmptySourceJoinKey() {
        return Objects.nonNull(sourceJoinKey);
    }

    public boolean notEmptyJoinKey() {
        return Objects.nonNull(joinKey);
    }
}
