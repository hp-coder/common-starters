package com.hp.biz.logger.factory;

import cn.hutool.core.util.StrUtil;
import com.hp.biz.logger.model.BizDiffDTO;
import com.hp.biz.logger.model.BizLogOperator;
import lombok.AllArgsConstructor;
import org.springframework.expression.EvaluationContext;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author hp
 */
@AllArgsConstructor
public class DefaultBizLogCreator extends AbstractBizLogCreator {

    private final int order;
    private final boolean preInvocation;
    private final Function<EvaluationContext, Boolean> condition;
    private final Function<EvaluationContext, String> title;
    private final Function<EvaluationContext, String> bizNo;
    private final Function<EvaluationContext, String> type;
    private final Function<EvaluationContext, String> subType;
    private final Function<EvaluationContext, String> scope;
    private final BizLogOperator operator;
    private final Function<EvaluationContext, String> successLog;
    private final Function<EvaluationContext, String> errorLog;
    private final Function<EvaluationContext, List<BizDiffDTO>> diff;

    @Override
    public int runOnLevel() {
        return this.order;
    }

    @Override
    public boolean preInvocation() {
        return this.preInvocation;
    }

    @Override
    protected boolean condition(EvaluationContext evaluationContext) {
        return Optional.ofNullable(this.condition).map(func -> func.apply(evaluationContext)).orElse(Boolean.TRUE);
    }

    @Override
    protected String title(EvaluationContext evaluationContext) {
        return Optional.ofNullable(this.title).map(func -> func.apply(evaluationContext)).orElse(StrUtil.EMPTY);
    }
    @Override
    protected String bizNo(EvaluationContext evaluationContext) {
        return Optional.ofNullable(this.bizNo).map(func -> func.apply(evaluationContext)).orElse(StrUtil.EMPTY);
    }

    @Override
    protected String type(EvaluationContext evaluationContext) {
        return Optional.ofNullable(this.type).map(func -> func.apply(evaluationContext)).orElse(StrUtil.EMPTY);
    }

    @Override
    protected String subType(EvaluationContext evaluationContext) {
        return Optional.ofNullable(this.subType).map(func -> func.apply(evaluationContext)).orElse(StrUtil.EMPTY);
    }

    @Override
    protected String scope(EvaluationContext evaluationContext) {
        return Optional.ofNullable(this.scope).map(func -> func.apply(evaluationContext)).orElse(StrUtil.EMPTY);
    }

    @Override
    protected BizLogOperator operator() {
        return this.operator;
    }

    @Override
    protected String successLog(EvaluationContext evaluationContext) {
        return Optional.ofNullable(this.successLog).map(func -> func.apply(evaluationContext)).orElse(StrUtil.EMPTY);
    }

    @Override
    protected String errorLog(EvaluationContext evaluationContext) {
        return Optional.ofNullable(this.errorLog).map(func -> func.apply(evaluationContext)).orElse(StrUtil.EMPTY);
    }

    @Override
    protected List<BizDiffDTO> diffs(EvaluationContext evaluationContext) {
        return Optional.ofNullable(this.diff).map(func -> func.apply(evaluationContext)).orElse(Collections.emptyList());
    }
}
