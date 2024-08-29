package com.hp.biz.logger.function;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ModifierUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.hp.biz.logger.annotation.BizLoggerComponent;
import com.hp.biz.logger.annotation.BizLoggerFunction;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author hp
 */
@Slf4j
public abstract class AbstractBizLoggerFunctionResolver implements IBizLoggerFunctionResolver {

    private final static Map<String, MethodWrapper> BIZ_LOG_FUNCTION_HOLDER = Maps.newHashMap();

    private final static Pattern functionLocator = Pattern.compile("#\\{\\s*(#\\w*)\\(\\s*(.*?)\\)}");

    protected final String pathJoin = StrUtil.DOT;

    protected final String spELBeanReferencePrefix = StrUtil.AT;

    protected final boolean overrideFunction;

    public AbstractBizLoggerFunctionResolver(boolean overrideFunction) {
        this.overrideFunction = overrideFunction;
    }

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        final Map<String, Object> components = applicationContext.getBeansWithAnnotation(BizLoggerComponent.class);
        if (MapUtil.isEmpty(components)) {
            log.warn("No custom biz log functions found, consider annotating classes with the @BizLoggerComponent.");
        }
        scanFunctions(components);
    }

    @Override
    public void registerFunctions(EvaluationContext evaluationContext) {
        if (evaluationContext instanceof StandardEvaluationContext sec) {
            BIZ_LOG_FUNCTION_HOLDER.forEach((name, methodWrapper) -> {
                if (methodWrapper.isStatic()) {
                    sec.registerFunction(name, methodWrapper.method());
                }
            });
            return;
        }
        log.warn("The {} provided is not a StandardEvaluationContext, Can not register functions on it.", evaluationContext.getClass());
    }

    @Override
    public String resolveFunctions(String expression) {
        final Matcher matcher = functionLocator.matcher(expression);
        while (matcher.find()) {
            String functionName = matcher.group(1);
            final String actualName = functionName.substring(1);
            if (BIZ_LOG_FUNCTION_HOLDER.containsKey(actualName)) {
                final MethodWrapper methodWrapper = BIZ_LOG_FUNCTION_HOLDER.get(actualName);
                if (methodWrapper.isStatic()) {
                    continue;
                }
                final String methodReference = spELBeanReferencePrefix + methodWrapper.beanName + pathJoin + methodWrapper.method().getName();
                expression = expression.replace(functionName, methodReference);
            }
        }
        return expression;
    }

    protected void scanFunctions(Map<String, Object> components) {
        components.forEach((name, component) -> {
            final List<Method> methods = MethodUtils.getMethodsListWithAnnotation(component.getClass(), BizLoggerFunction.class);
            if (CollUtil.isEmpty(methods)) {
                log.warn("The Biz-Logger component {} has no methods defined.", component.getClass().getName());
                return;
            }

            final BizLoggerComponent bizLoggerComponent = AnnotatedElementUtils.findMergedAnnotation(component.getClass(), BizLoggerComponent.class);
            assert bizLoggerComponent != null;
            final String beanName = bizLoggerComponent.value();

            methods.forEach(method -> {
                        final BizLoggerFunction bizLogFunction = AnnotationUtils.findAnnotation(method, BizLoggerFunction.class);
                        if (Objects.isNull(bizLogFunction)) {
                            return;
                        }
                        putFunction(beanName, bizLogFunction.value(), method);
                    });
        });
    }

    protected void putFunction(String beanName, String methodName, Method method) {
        Preconditions.checkArgument(StrUtil.isNotEmpty(methodName), "Function name cant be empty.");
        Preconditions.checkArgument(Objects.nonNull(method), "Function cant be empty.");
        final MethodWrapper methodWrapper = new MethodWrapper(method, ModifierUtil.isStatic(method), beanName);
        if (overrideFunction) {
            BIZ_LOG_FUNCTION_HOLDER.put(methodName, methodWrapper);
        } else {
            BIZ_LOG_FUNCTION_HOLDER.putIfAbsent(methodName, methodWrapper);
        }
    }

    private record MethodWrapper(Method method, boolean isStatic, String beanName) {

        public MethodWrapper {
            Preconditions.checkArgument(Objects.nonNull(method), "The custom function method cant be null.");
            Preconditions.checkArgument(StrUtil.isNotEmpty(beanName), "The custom function class name or bean name cant be null.");
        }
    }
}
