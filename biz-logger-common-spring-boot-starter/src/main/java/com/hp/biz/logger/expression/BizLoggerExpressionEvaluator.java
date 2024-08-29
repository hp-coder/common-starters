package com.hp.biz.logger.expression;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import com.hp.biz.logger.function.IBizLoggerFunctionResolver;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.lang.reflect.AnnotatedElement;
import java.util.Map;

/**
 * @author hp
 */
@Slf4j
public class BizLoggerExpressionEvaluator extends CachedExpressionEvaluator {

    private final Map<ExpressionKey, Expression> expressionCache = Maps.newConcurrentMap();
    private final ParserContext parserContext = ParserContext.TEMPLATE_EXPRESSION;
    private final ExpressionParser expressionParser;
    private final IBizLoggerFunctionResolver bizLoggerFunctionResolver;

    public BizLoggerExpressionEvaluator(IBizLoggerFunctionResolver bizLoggerFunctionResolver,
                                        SpelExpressionParser expressionParser
    ) {
        super(expressionParser);
        this.expressionParser = expressionParser;
        this.bizLoggerFunctionResolver = bizLoggerFunctionResolver;
    }

    public <T> T getValue(String expression, Class<?> targetClass, AnnotatedElement annotatedElement, EvaluationContext evaluationContext) {
        if (StrUtil.isEmpty(expression)) {
            return null;
        }
        return getValue(expression, new AnnotatedElementKey(annotatedElement, targetClass), evaluationContext);
    }

    @SuppressWarnings("unchecked")
    protected <T> T getValue(String expression, AnnotatedElementKey methodKey, EvaluationContext evaluationContext) {
        final String resolvedExpression = bizLoggerFunctionResolver.resolveFunctions(expression);
        log.debug("The expression being resolved is {}", resolvedExpression);
        return (T) getExpression(this.expressionCache, methodKey, resolvedExpression).getValue(evaluationContext);
    }

    @Nonnull
    @Override
    protected Expression parseExpression(@Nonnull String expression) {
        return expressionParser.parseExpression(expression, parserContext);
    }
}
