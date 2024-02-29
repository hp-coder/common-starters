package com.hp.joininmemory.support;

import cn.hutool.core.util.StrUtil;
import com.hp.joininmemory.annotation.JoinInMemory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.*;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author hp 2023/3/27
 */
@Slf4j
public class JoinInMemoryBasedJoinFieldExecutorFactory extends AbstractAnnotationBasedJoinFieldExecutorFactory<JoinInMemory> {

    private final ExpressionParser expressionParser = new SpelExpressionParser();
    private final ParserContext parserContext = ParserContext.TEMPLATE_EXPRESSION;
    private final BeanResolver beanResolver;

    public JoinInMemoryBasedJoinFieldExecutorFactory(BeanResolver beanResolver) {
        super(JoinInMemory.class);
        this.beanResolver = beanResolver;
    }

    @Override
    protected <DATA> int createRunLevel(Class<DATA> clazz, Field field, JoinInMemory annotation) {
        log.debug("run level for class {} field {}, is {}", clazz, field.getName(), annotation.runLevel());
        return annotation.runLevel().getCode();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <DATA, SOURCE_JOIN_KEY, JOIN_KEY> Function<SOURCE_JOIN_KEY, JOIN_KEY> createSourceDataKeyConverter(Class<DATA> clazz, Field field, JoinInMemory annotation) {
        if (StringUtils.isEmpty(annotation.sourceDataKeyConverter())) {
            log.debug("No source data key converter for class {}, field {}", clazz, field.getName());
            return (Function<SOURCE_JOIN_KEY, JOIN_KEY>) Function.identity();
        } else {
            log.debug("The source data key converter for class {} field {}, is {}", clazz, field.getName(), annotation.joinDataConverter());
            return new DataGetter<>(annotation.sourceDataKeyConverter());
        }
    }

    @Override
    protected <DATA, SOURCE_JOIN_KEY> Function<DATA, SOURCE_JOIN_KEY> createKeyFromSourceData(Class<DATA> clazz, Field field, JoinInMemory annotation) {
        log.debug("Key from source data for class {} field {}, is {}", clazz, field.getName(), annotation.keyFromJoinData());
        return new DataGetter<>(annotation.keyFromSourceData());
    }

    @Override
    protected <DATA, JOIN_KEY, JOIN_DATA> Function<Collection<JOIN_KEY>, List<JOIN_DATA>> createLoader(Class<DATA> clazz, Field field, JoinInMemory annotation) {
        log.debug("data loader for class {}  field {}, is {}", clazz, field.getName(), annotation.loader());
        return new DataGetter<>(annotation.loader());
    }

    @Override
    protected <DATA, JOIN_DATA, DATA_JOIN_KEY> Function<JOIN_DATA, DATA_JOIN_KEY> createKeyFromJoinData(Class<DATA> clazz, Field field, JoinInMemory annotation) {
        log.debug("The key from join data is {}, the class is {}, the field is {}", annotation.keyFromJoinData(), clazz, field.getName());
        return new DataGetter<>(annotation.keyFromJoinData());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <DATA, JOIN_KEY, DATA_JOIN_KEY> Function<DATA_JOIN_KEY, JOIN_KEY> createJoinDataKeyConverter(Class<DATA> clazz, Field field, JoinInMemory annotation) {
        if (StringUtils.isEmpty(annotation.joinDataKeyConverter())) {
            log.debug("No join data key converter for class {}, field {}", clazz, field.getName());
            return (Function<DATA_JOIN_KEY, JOIN_KEY>) Function.identity();
        } else {
            log.debug("The join data key converter for class {} field {}, is {}", clazz, field.getName(), annotation.joinDataConverter());
            return new DataGetter<>(annotation.joinDataKeyConverter());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <DATA, JOIN_DATA, JOIN_RESULT> Function<JOIN_DATA, JOIN_RESULT> createJoinDataConverter(Class<DATA> clazz, Field field, JoinInMemory annotation) {
        if (StringUtils.isEmpty(annotation.joinDataConverter())) {
            log.debug("No data convert for class {}, field {}", clazz, field.getName());
            return (Function<JOIN_DATA, JOIN_RESULT>) Function.identity();
        } else {
            log.debug("The data-converter for class {} field {}, is {}", clazz, field.getName(), annotation.joinDataConverter());
            return new DataGetter<>(annotation.joinDataConverter());
        }
    }

    @Override
    protected <DATA, JOIN_RESULT> BiConsumer<DATA, List<JOIN_RESULT>> createFoundFunction(Class<DATA> clazz, Field field, JoinInMemory annotation) {
        log.debug("the field about to be set is {}, a type of {}", field.getName(), clazz);
        boolean isCollection = Collection.class.isAssignableFrom(field.getType());
        return new DataSetter<>(field.getName(), isCollection);
    }

    @Override
    public <DATA> Function<Class<? extends Annotation>, String> groupBy(Class<DATA> clazz, Field field, JoinInMemory annotation) {
        return rootAnnotation -> rootAnnotation.getName() +
                annotation.keyFromJoinData() +
                annotation.joinDataKeyConverter() +
                annotation.loader() +
                annotation.runLevel() +
                annotation.filter();
    }

    private class DataSetter<T, R> implements BiConsumer<T, List<R>> {
        private final String fieldName;
        private final boolean isCollection;
        private final Expression expression;

        private DataSetter(String fieldName, boolean isCollection) {
            this.fieldName = fieldName;
            this.expression = expressionParser.parseExpression(fieldName);
            this.isCollection = isCollection;
        }

        @Override
        public void accept(T data, List<R> result) {
            if (isCollection) {
                this.expression.setValue(data, result);
            } else {
                int size = result.size();
                if (size == 1) {
                    this.expression.setValue(data, result.get(0));
                } else {
                    log.error("write join result to {} error, field is {}, data is {}", data, fieldName, result);
                }
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
