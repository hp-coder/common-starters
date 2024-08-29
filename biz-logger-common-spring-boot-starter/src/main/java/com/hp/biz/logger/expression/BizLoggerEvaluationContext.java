package com.hp.biz.logger.expression;

import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.ParameterNameDiscoverer;

import java.lang.reflect.Method;

/**
 * @author hp
 */
public class BizLoggerEvaluationContext extends MethodBasedEvaluationContext {

    public BizLoggerEvaluationContext(
            Object rootObject,
            Method method,
            Object[] arguments,
            ParameterNameDiscoverer parameterNameDiscoverer
    ) {
        super(rootObject, method, arguments, parameterNameDiscoverer);
    }
}

