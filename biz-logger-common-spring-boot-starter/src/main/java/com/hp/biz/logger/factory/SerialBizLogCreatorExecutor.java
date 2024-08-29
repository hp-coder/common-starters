package com.hp.biz.logger.factory;

import cn.hutool.core.collection.CollUtil;
import com.hp.biz.logger.model.BizLogDTO;
import com.hp.biz.logger.model.MethodInvocationWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.util.StopWatch;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author hp
 */
@Slf4j
public class SerialBizLogCreatorExecutor extends AbstractBizLogCreatorsExecutor {

    public SerialBizLogCreatorExecutor(
            BeanResolver beanResolver,
            List<IBizLogCreator> creators
    ) {
        super(beanResolver, creators);
    }

    @Override
    public Collection<BizLogDTO> execute(MethodInvocationWrapper invocationWrapper, boolean preInvocation) {
        if (CollUtil.isEmpty(getCreators())) {
            return Collections.emptyList();
        }
        final EvaluationContext evaluationContext = BizLoggerEvaluationContextFactory.createBizLoggerEvaluationContext(
                invocationWrapper.getMethod(),
                invocationWrapper.getArgs(),
                invocationWrapper.getTargetClass(),
                getBeanResolver()
        );

        final List<IBizLogCreator> orderedCreators = getCreators()
                .stream()
                .filter(c -> c.preInvocation() == preInvocation)
                .sorted(Comparator.comparing(IBizLogCreator::runOnLevel))
                .toList();

        if (CollUtil.isEmpty(orderedCreators)) {
            return Collections.emptyList();
        }

        return orderedCreators.stream()
                .map(creator -> {
                    if (log.isDebugEnabled()) {
                        StopWatch stopwatch = new StopWatch("Start creating biz logs");
                        stopwatch.start();
                        final BizLogDTO bizLogDTO = creator.createLog(evaluationContext);
                        stopwatch.stop();
                        log.debug("Run creation cost {} ms, executor is {}.", stopwatch.getTotalTimeMillis(), creator);
                        return bizLogDTO;
                    } else {
                        return creator.createLog(evaluationContext);
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
