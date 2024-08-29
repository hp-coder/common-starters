package com.hp.biz.logger.function;

import org.springframework.context.ApplicationContextAware;
import org.springframework.expression.EvaluationContext;

/**
 * @author hp
 */
public interface IBizLoggerFunctionRegistrar extends ApplicationContextAware {

    void registerFunctions(EvaluationContext evaluationContext);

}
