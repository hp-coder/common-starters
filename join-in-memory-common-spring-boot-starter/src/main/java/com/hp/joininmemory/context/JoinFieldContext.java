package com.hp.joininmemory.context;

import com.hp.joininmemory.support.AbstractJoinFieldV2Executor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

/**
 * A context that holds join information of each join field
 *
 * @author <a href="mailto:max_verstrappon@outlook.com">HuPeng</a>
 */
@Getter
@Setter
public class JoinFieldContext<SOURCE_DATA, JOIN_KEY, JOIN_DATA, JOIN_RESULT> {

    /**
     * The executor that actually dose the join process
     */
    private final AbstractJoinFieldV2Executor<SOURCE_DATA, JOIN_KEY, JOIN_DATA, JOIN_RESULT> executor;

    /**
     * The source data object
     */
    private SOURCE_DATA sourceData;

    /**
     * The join key of the source data object
     */
    private JOIN_KEY joinKey;

    /**
     * The join data list, which are fetched through join keys
     */
    private List<JOIN_DATA> joinData;

    /**
     * The join result list, which are converted from join data list through a predefined mapping process
     */
    private JOIN_RESULT joinResult;

    public JoinFieldContext(
            AbstractJoinFieldV2Executor<SOURCE_DATA, JOIN_KEY, JOIN_DATA, JOIN_RESULT> executor,
            SOURCE_DATA sourceData
    ) {
        this.sourceData = sourceData;
        this.executor = executor;
    }

    public boolean notEmptyJoinKey() {
        return Objects.nonNull(joinKey);
    }
}
