package com.hp.biz.logger.factory;

import cn.hutool.extra.spring.SpringUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hp.biz.logger.context.BizLoggerContext;
import com.hp.biz.logger.expression.BizLoggerEvaluationContext;
import com.hp.biz.logger.function.IBizLoggerFunctionRegistrar;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author hp
 */
public class BizLoggerEvaluationContextFactory {

    private static final Map<AnnotatedElementKey, Method> targetMethodCache = Maps.newConcurrentMap();

    public static EvaluationContext createStandardEvaluationContext(Object rootObject, BeanResolver beanResolver) {
        final StandardEvaluationContext evaluationContext = new StandardEvaluationContext(rootObject);
        setBeanResolver(evaluationContext, beanResolver);
        registerFunctions(getFunctionRegistrars(), evaluationContext);
        return evaluationContext;
    }

    public static EvaluationContext createBizLoggerEvaluationContext(
            Method method,
            Object[] args,
            Class<?> targetClass,
            BeanResolver beanResolver
    ) {
        final Method targetMethod = targetMethodCache.computeIfAbsent(
                new AnnotatedElementKey(method, targetClass),
                k -> AopUtils.getMostSpecificMethod(method, targetClass)
        );
        final BizLoggerEvaluationContext evaluationContext = new BizLoggerEvaluationContext(
                null,
                targetMethod,
                args,
                new DefaultParameterNameDiscoverer()
        );
        if (BizLoggerContext.notEmpty()) {
            BizLoggerContext.getAllVariables().forEach(evaluationContext::setVariable);
        }

        setBeanResolver(evaluationContext, beanResolver);

        registerFunctions(getFunctionRegistrars(), evaluationContext);

        return evaluationContext;
    }

    private static void setBeanResolver(EvaluationContext evaluationContext, BeanResolver beanResolver) {
        if (evaluationContext instanceof StandardEvaluationContext standardEvaluationContext) {
            Optional.ofNullable(beanResolver).ifPresent(standardEvaluationContext::setBeanResolver);
        }
    }

    private static List<IBizLoggerFunctionRegistrar> getFunctionRegistrars() {
        final Map<String, IBizLoggerFunctionRegistrar> functionRegistrarMapping = SpringUtil.getBeansOfType(IBizLoggerFunctionRegistrar.class);
        final List<IBizLoggerFunctionRegistrar> functionRegistrars = Lists.newArrayList(functionRegistrarMapping.values());
        AnnotationAwareOrderComparator.sort(functionRegistrars);
        return functionRegistrars;
    }

    private static void registerFunctions(List<IBizLoggerFunctionRegistrar> bizLoggerFunctionRegistrars, EvaluationContext evaluationContext) {
        Preconditions.checkArgument(
                Objects.nonNull(evaluationContext),
                "The evaluationContext cant be null when registering custom functions."
        );
        bizLoggerFunctionRegistrars
                .forEach(registrar -> registrar.registerFunctions(evaluationContext));
    }
}

