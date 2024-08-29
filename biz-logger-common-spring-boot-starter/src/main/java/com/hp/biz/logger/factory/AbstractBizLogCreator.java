package com.hp.biz.logger.factory;

import com.hp.biz.logger.context.BizLoggerContext;
import com.hp.biz.logger.model.BizDiffDTO;
import com.hp.biz.logger.model.BizLogDTO;
import com.hp.biz.logger.model.BizLogOperator;
import org.springframework.expression.EvaluationContext;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * @author hp
 */
public abstract class AbstractBizLogCreator implements IBizLogCreator {

    protected abstract boolean condition(EvaluationContext evaluationContext);

    protected abstract String title(EvaluationContext evaluationContext);

    protected abstract String bizNo(EvaluationContext evaluationContext);

    protected abstract String type(EvaluationContext evaluationContext);

    protected abstract String subType(EvaluationContext evaluationContext);

    protected abstract String scope(EvaluationContext evaluationContext);

    protected abstract BizLogOperator operator();

    protected abstract String successLog(EvaluationContext evaluationContext);

    protected abstract String errorLog(EvaluationContext evaluationContext);

    protected abstract List<BizDiffDTO> diffs(EvaluationContext evaluationContext);

    @Override
    public BizLogDTO createLog(EvaluationContext evaluationContext) {
        if (!condition(evaluationContext)) {
            return null;
        }
        final BizLogDTO bizLogDTO = BizLogDTO.builder()
                .tenant(BizLoggerContext.getTenant())
                .title(title(evaluationContext))
                .bizNo(bizNo(evaluationContext))
                .type(type(evaluationContext))
                .subType(subType(evaluationContext))
                .scope(scope(evaluationContext))
                .operator(operator())
                .operatedAt(Instant.now())
                .invocationInfo(BizLoggerContext.getInvocationInfo())
                .diffs(diffs(evaluationContext))
                .build();
        Optional.ofNullable(BizLoggerContext.getThrowable())
                .ifPresentOrElse(
                        thr -> bizLogDTO.failed(errorLog(evaluationContext)),
                        () -> bizLogDTO.succeed(successLog(evaluationContext))
                );
        return bizLogDTO;
    }
}
