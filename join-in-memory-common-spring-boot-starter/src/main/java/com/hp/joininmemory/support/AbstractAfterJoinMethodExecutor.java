package com.hp.joininmemory.support;

import com.hp.joininmemory.AfterJoinMethodExecutor;
import com.hp.joininmemory.exception.JoinErrorCode;
import com.hp.joininmemory.exception.JoinException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author hp
 */
@Slf4j
public abstract class AbstractAfterJoinMethodExecutor<DATA_AFTER_JOIN> implements AfterJoinMethodExecutor<DATA_AFTER_JOIN> {
    protected abstract void afterJoin(DATA_AFTER_JOIN data);

    @Override
    public void execute(DATA_AFTER_JOIN data) {
        log.debug("Executing after join method on {}", data);
        try {
            afterJoin(data);
        } catch (Exception e) {
            log.error("AfterJoinErrorMessage={}||data={}", JoinErrorCode.AFTER_JOIN_ERROR.getName(), data.toString(), e);
            throw new JoinException(JoinErrorCode.AFTER_JOIN_ERROR, e);
        }
    }
}
