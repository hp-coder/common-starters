package com.hp.joininmemory.aspect;

import cn.hutool.core.util.StrUtil;
import com.hp.joininmemory.JoinService;
import com.hp.joininmemory.annotation.JoinAtReturn;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.*;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.function.Function;

/**
 * @author hp
 * @since 1.0.1-sp3.2-SNAPSHOT
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class JoinAtReturnAdvice {

    private final JoinService joinService;
    private final BeanResolver beanResolver;
    private final ExpressionParser expressionParser = new SpelExpressionParser();
    private final ParserContext parserContext = ParserContext.TEMPLATE_EXPRESSION;


    @Pointcut(value = "@annotation(com.hp.joininmemory.annotation.JoinAtReturn)")
    public void joinAtReturn() {
    }

    @AfterReturning(value = "joinAtReturn()", returning = "returnValue")
    public void afterReturning(JoinPoint joinPoint, Object returnValue) {
        final Signature signature = joinPoint.getSignature();
        if (signature instanceof MethodSignature methodSignature) {
            final Method method = methodSignature.getMethod();
            final JoinAtReturn joinAtReturn = method.getAnnotation(JoinAtReturn.class);
            Object joinData = returnValue;
            if (StrUtil.isNotEmpty(joinAtReturn.value())) {
                final DataGetter<Object, Object> returnValueGetter = new DataGetter<>(joinAtReturn.value());
                joinData = returnValueGetter.apply(returnValue);
            }
            if (Collection.class.isAssignableFrom(joinData.getClass())) {
                joinService.joinInMemory((Collection<?>) joinData);
            } else {
                joinService.joinInMemory(joinData);
            }
        }
    }

    private class DataGetter<T, R> implements Function<T, R> {
        private final Expression expression;
        private final EvaluationContext evaluationContext;

        private DataGetter(String expStr) {
            if (StrUtil.isNotEmpty(expStr) && expStr.startsWith(parserContext.getExpressionPrefix())) {
                this.expression = expressionParser.parseExpression(expStr, parserContext);
            } else {
                this.expression = expressionParser.parseExpression(expStr);
            }
            final StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
            evaluationContext.setBeanResolver(beanResolver);
            this.evaluationContext = evaluationContext;
        }

        @SuppressWarnings("unchecked")
        @Override
        public R apply(Object data) {
            if (data == null) {
                return null;
            }
            return (R) expression.getValue(evaluationContext, data);
        }
    }
}
