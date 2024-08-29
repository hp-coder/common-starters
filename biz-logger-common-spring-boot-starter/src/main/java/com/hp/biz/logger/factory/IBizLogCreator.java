package com.hp.biz.logger.factory;

import com.hp.biz.logger.model.BizLogDTO;
import org.springframework.expression.EvaluationContext;

/**
 * @author hp
 */
public interface IBizLogCreator {

    BizLogDTO createLog(EvaluationContext evaluationContext);

    default boolean preInvocation() {
        return false;
    }

    default int runOnLevel() {
        return 0;
    }
}
