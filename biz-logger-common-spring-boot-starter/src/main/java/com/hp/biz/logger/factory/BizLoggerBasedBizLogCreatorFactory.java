package com.hp.biz.logger.factory;

import com.hp.biz.logger.annotation.BizDiffer;
import com.hp.biz.logger.annotation.BizLogger;
import com.hp.biz.logger.context.BizLoggerContext;
import com.hp.biz.logger.expression.BizLoggerExpressionEvaluator;
import com.hp.biz.logger.function.IBizLoggerFunctionResolver;
import com.hp.biz.logger.model.BizDiffDTO;
import com.hp.biz.logger.model.BizDiffObjectWrapper;
import com.hp.biz.logger.model.BizLogOperator;
import com.hp.biz.logger.service.IBizLogOperatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Function;

/**
 * @author hp
 */
@Slf4j
public class BizLoggerBasedBizLogCreatorFactory extends AbstractAnnotationBasedBizLogCreatorFactory<BizLogger> {

    private final BizLoggerExpressionEvaluator bizLoggerExpressionEvaluator;
    private final IBizLogOperatorService bizLogOperatorService;

    public BizLoggerBasedBizLogCreatorFactory(
            ObjectProvider<IBizLogOperatorService> bizLogOperatorService,
            ObjectProvider<IBizLoggerFunctionResolver> bizLoggerFunctionResolver
    ) {
        super(BizLogger.class);
        this.bizLogOperatorService = bizLogOperatorService.getIfAvailable();
        this.bizLoggerExpressionEvaluator = new BizLoggerExpressionEvaluator(bizLoggerFunctionResolver.getIfAvailable(), new SpelExpressionParser());
    }

    @Override
    protected int createForOrder(Class<?> targetClass, Method method, BizLogger annotation) {
        return annotation.order();
    }

    @Override
    protected boolean createForPreInvocation(Class<?> targetClass, Method method, BizLogger annotation) {
        return annotation.preInvocation();
    }

    @Override
    protected Function<EvaluationContext, String> createForTitle(Class<?> targetClass, Method method, BizLogger annotation) {
        return context -> stringify(bizLoggerExpressionEvaluator.getValue(annotation.title(), targetClass, method, context));
    }

    @Override
    protected Function<EvaluationContext, String> createForBizNo(Class<?> targetClass, Method method, BizLogger annotation) {
        return context -> stringify(bizLoggerExpressionEvaluator.getValue(annotation.bizNo(), targetClass, method, context));
    }

    @Override
    protected Function<EvaluationContext, String> createForType(Class<?> targetClass, Method method, BizLogger annotation) {
        return context -> stringify(bizLoggerExpressionEvaluator.getValue(annotation.type(), targetClass, method, context));
    }

    @Override
    protected Function<EvaluationContext, String> createForSubType(Class<?> targetClass, Method method, BizLogger annotation) {
        return context -> stringify(bizLoggerExpressionEvaluator.getValue(annotation.subType(), targetClass, method, context));
    }

    @Override
    protected Function<EvaluationContext, String> createForScope(Class<?> targetClass, Method method, BizLogger annotation) {
        return context -> stringify(bizLoggerExpressionEvaluator.getValue(annotation.scope(), targetClass, method, context));
    }

    @Override
    protected BizLogOperator createForOperator(Class<?> targetClass, Method method, BizLogger annotation) {
        return bizLogOperatorService.get();
    }

    @Override
    protected Function<EvaluationContext, String> createForSuccessLog(Class<?> targetClass, Method method, BizLogger annotation) {
        return context -> stringify(bizLoggerExpressionEvaluator.getValue(annotation.successLog(), targetClass, method, context));
    }

    @Override
    protected Function<EvaluationContext, String> createForErrorLog(Class<?> targetClass, Method method, BizLogger annotation) {
        return context -> stringify(bizLoggerExpressionEvaluator.getValue(annotation.errorLog(), targetClass, method, context));
    }

    @Override
    protected Function<EvaluationContext, Boolean> createForCondition(Class<?> targetClass, Method method, BizLogger annotation) {
        return context -> bizLoggerExpressionEvaluator.getValue(annotation.condition(), targetClass, method, context);
    }

    @Override
    protected Function<EvaluationContext, List<BizDiffDTO>> createForDiffs(Class<?> targetClass, Method method, BizLogger annotation) {
        return context -> {
            final BizDiffer diff = annotation.diff();
            String expression;
            if (diff.ignored()) {
                expression = "#{#BIZ_DIFFER(#" + BizLoggerContext.DIFF_OBJECTS_KEY + ")}";
            } else {
                final String wrapperClassName = BizDiffObjectWrapper.class.getName();
                expression = "#{#BIZ_DIFFER( new " + wrapperClassName + "(" + diff.before() + ", " + diff.after() + "))}";
            }
            return bizLoggerExpressionEvaluator.getValue(expression, targetClass, method, context);
        };
    }
}
