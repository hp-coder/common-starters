package com.luban.joininmemory.support;

import com.luban.joininmemory.annotation.JoinInMemory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

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

    private final ExpressionParser parser = new SpelExpressionParser();
    private final TemplateParserContext templateParserContext = new TemplateParserContext();
    private final BeanResolver beanResolver;

    public JoinInMemoryBasedJoinFieldExecutorFactory(BeanResolver beanResolver) {
        super(JoinInMemory.class);
        this.beanResolver = beanResolver;
    }

    @Override
    protected <DATA> BiConsumer<Object, List<Object>> createFoundFunction(Class<DATA> clazz, Field field, JoinInMemory annotation) {
        log.info("the field about to be set is {}, a type of {}", field.getName(), clazz);
        boolean isCollection = Collection.class.isAssignableFrom(field.getType());
        return new DataSetter(field.getName(), isCollection);
    }

    @Override
    protected <DATA> Function<Object, Object> createDataConverter(Class<DATA> clazz, Field field, JoinInMemory annotation) {
        if (StringUtils.isEmpty(annotation.joinDataConverter())) {
            log.debug("No data convert for class {}, field {}", clazz, field.getName());
            return Function.identity();
        } else {
            log.debug("The data-converter for class {} field {}, is {}", clazz, field.getName(), annotation.joinDataConverter());
            return new DataGetter<>(annotation.joinDataConverter());
        }
    }

    @Override
    protected <DATA> Function<Object, Object> createKeyGeneratorFromJoinData(Class<DATA> clazz, Field field, JoinInMemory annotation) {
        log.debug("The key from join data is {}, the class is {}, the field is {}", annotation.keyFromJoinData(), clazz, field.getName());
        return new DataGetter<>(annotation.keyFromJoinData());
    }

    @Override
    protected <DATA> Function<Object, Object> createKeyConverterFromJoinData(Class<DATA> clazz, Field field, JoinInMemory annotation) {
        if (StringUtils.isEmpty(annotation.joinDataKeyConverter())) {
            log.debug("No join data key converter for class {}, field {}", clazz, field.getName());
            return Function.identity();
        } else {
            log.debug("The join data key converter for class {} field {}, is {}", clazz, field.getName(), annotation.joinDataConverter());
            return new DataGetter<>(annotation.joinDataKeyConverter());
        }
    }

    @Override
    protected <DATA> Function<List<Object>, List<Object>> createDataLoader(Class<DATA> clazz, Field field, JoinInMemory annotation) {
        log.debug("data loader for class {}  field {}, is {}", clazz, field.getName(), annotation.loader());
        return new DataGetter<>(annotation.loader());
    }

    @Override
    protected <DATA> Function<Object, Object> createKeyGeneratorFromSourceData(Class<DATA> clazz, Field field, JoinInMemory annotation) {
        log.debug("Key from source data for class {} field {}, is {}", clazz, field.getName(), annotation.keyFromJoinData());
        return new DataGetter<>(annotation.keyFromSourceData());
    }

    @Override
    protected <DATA> Function<Object, Object> createKeyConverterFromSourceData(Class<DATA> clazz, Field field, JoinInMemory annotation) {
        if (StringUtils.isEmpty(annotation.sourceDataKeyConverter())) {
            log.debug("No source data key converter for class {}, field {}", clazz, field.getName());
            return Function.identity();
        } else {
            log.debug("The source data key converter for class {} field {}, is {}", clazz, field.getName(), annotation.joinDataConverter());
            return new DataGetter<>(annotation.sourceDataKeyConverter());
        }
    }

    @Override
    protected <DATA> int createRunLevel(Class<DATA> clazz, Field field, JoinInMemory annotation) {
        log.debug("run level for class {} field {},  is {}", clazz, field.getName(), annotation.runLevel());
        return annotation.runLevel();
    }

    private class DataSetter implements BiConsumer<Object, List<Object>> {
        private final String fieldName;
        private final boolean isCollection;
        private final Expression expression;

        private DataSetter(String fieldName, boolean isCollection) {
            this.fieldName = fieldName;
            this.expression = parser.parseExpression(fieldName);
            this.isCollection = isCollection;
        }

        @Override
        public void accept(Object data, List<Object> result) {
            if (isCollection) {
                this.expression.setValue(data, result);
            } else {
                int size = result.size();
                if (size == 1) {
                    this.expression.setValue(data, result.get(0));
                } else {
                    log.warn("write join result to {} error, field is {}, data is {}", data, fieldName, result);
                }
            }
        }
    }

    private class DataGetter<T, R> implements Function<T, R> {
        private final Expression expression;
        private final EvaluationContext evaluationContext;

        private DataGetter(String expStr) {
            this.expression = parser.parseExpression(expStr, templateParserContext);
            StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
            evaluationContext.setBeanResolver(beanResolver);
            this.evaluationContext = evaluationContext;
        }

        @Override
        public Object apply(Object data) {
            if (data == null) {
                return null;
            }
            return expression.getValue(evaluationContext, data);
        }
    }
}
