package com.hp.spiderman.factory.support;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.hp.common.base.annotations.MethodDesc;
import com.hp.spiderman.annotations.SpiderNode;
import com.hp.spiderman.constants.NodeRetrieveLogic;
import com.hp.spiderman.factory.ConnectionFactory;
import com.hp.spiderman.factory.SpiderManService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This is where the actual execution processes are defined.
 * <p>
 * The Executor Class {@link DefaultDomNodeExecutorAdapter} was designed as a template
 * to hold and execute actual functions provided by this factory class.
 *
 * @author hp
 */
@Slf4j
public class SpiderNodeBasedDomNodeExecutorFactory extends AbstractAnnotationBasedDomNodeExecutorFactory<SpiderNode> {

    private final ExpressionParser expressionParser = new SpelExpressionParser();
    private final TemplateParserContext templateParserContext = new TemplateParserContext();
    private final BeanResolver beanResolver;
    private final ConnectionFactory connectionFactory;

    public SpiderNodeBasedDomNodeExecutorFactory(ConnectionFactory connectionFactory, BeanResolver beanResolver) {
        super(SpiderNode.class);
        this.connectionFactory = connectionFactory;
        this.beanResolver = beanResolver;
    }

    @MethodDesc("By default, using CSS selector to filter nodes")
    @Override
    protected <DATA> Function<Document, List<Object>> createNodeFilters(Class<DATA> clazz, Field field, SpiderNode annotation) {
        if (ArrayUtil.isEmpty(annotation.filters())) {
            return (document) -> null;
        }
        if (StrUtil.isEmpty(annotation.node())) {
            return (document) -> null;
        }
        return (document) -> {
            final String cssSelector = Arrays.stream(annotation.filters())
                    .filter(filter -> StrUtil.isNotEmpty(filter.attr()))
                    .map(filter -> String.format(
                            "[%s%s%s]",
                            filter.attr(),
                            filter.val() != null ? filter.logic().getCode() : "",
                            filter.val() != null ? filter.val() : "")
                    )
                    .collect(Collectors.joining());
            final Elements elements = document.select(String.format("%s%s", annotation.node(), cssSelector));
            if (annotation.nodeRetriever().logic().equals(NodeRetrieveLogic.SELF)) {
                return Arrays.asList(elements.toArray());
            }
            if (annotation.nodeRetriever().logic().equals(NodeRetrieveLogic.CHILD)) {
                return elements.stream()
                        .map(element -> element.childNode(annotation.nodeRetriever().childIndex()))
                        .collect(Collectors.toList());
            }
            if (annotation.nodeRetriever().logic().equals(NodeRetrieveLogic.PARENT)) {
                return elements.stream()
                        .map(Element::parent)
                        .collect(Collectors.toList());
            }
            throw new IllegalArgumentException("Not Supported Operation");
        };
    }

    @Override
    protected <DATA> Function<Object, Object> createDataRetriever(Class<DATA> clazz, Field field, SpiderNode annotation) {
        if (StrUtil.isEmpty(annotation.retriever())) {
            return Function.identity();
        }
        return new DataGetter<>(annotation.retriever());
    }

    @Override
    protected <DATA> Function<Object, Object> createDataConverter(Class<DATA> clazz, Field field, SpiderNode annotation) {
        if (StrUtil.isEmpty(annotation.converter())) {
            return Function.identity();
        }
        return new DataGetter<>(annotation.converter());
    }

    @Override
    protected <DATA> Function<Object, Object> createBeforeOnFound(Class<DATA> clazz, Field field, SpiderNode annotation) {
        log.debug("Theoretically, the returning object is supposed to be a complex object annotated with Spider-Config.");
        return obj -> {
            final boolean collection = Collection.class.isAssignableFrom(field.getType());
            Class<?> type;
            if (collection) {
                type = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
            } else {
                type = field.getType();
            }
            boolean primitive = type.isPrimitive();
            if (!primitive) {
                try {
                    final Field encapsulatedType = type.getField("TYPE");
                    primitive = encapsulatedType != null && ((Class) encapsulatedType.get(null)).isPrimitive();
                } catch (Exception ignore) {}
            }
            final boolean string = String.class.isAssignableFrom(type);
            if (primitive || string) {
                return obj;
            }
            final List<Connection> connections = connectionFactory.createFor(type, obj);
            final SpiderManService jsoupService = SpringUtil.getBean(SpiderManService.class);
            return jsoupService.spider(type, connections);
        };
    }

    @Override
    protected <DATA> BiConsumer<Object, List<Object>> createOnFound(Class<DATA> clazz, Field field, SpiderNode annotation) {
        boolean isCollection = Collection.class.isAssignableFrom(field.getType());
        return new DataSetter(field.getName(), isCollection);
    }

    @Override
    protected <DATA> int createRunLevel(Class<DATA> clazz, Field field, SpiderNode annotation) {
        return annotation.runOnLevel();
    }

    private class DataSetter implements BiConsumer<Object, List<Object>> {
        private final String fieldName;
        private final boolean isCollection;
        private final Expression expression;

        public DataSetter(String fieldName, boolean isCollection) {
            this.fieldName = fieldName;
            this.isCollection = isCollection;
            this.expression = expressionParser.parseExpression(fieldName);
        }

        @Override
        public void accept(Object data, List<Object> results) {
            if (isCollection) {
                this.expression.setValue(data, results);
            } else {
                int size = results.size();
                if (size == 1) {
                    this.expression.setValue(data, results.get(0));
                } else {
                    log.debug(" the result for field: '{}' is empty.", fieldName);
                }
            }
        }
    }

    private class DataGetter<T, R> implements Function<T, R> {
        private final Expression expression;
        private final EvaluationContext evaluationContext;

        public DataGetter(String expressionStr) {
            this.expression = expressionParser.parseExpression(expressionStr, templateParserContext);
            final StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
            evaluationContext.setBeanResolver(beanResolver);
            this.evaluationContext = evaluationContext;
        }

        @Override
        public Object apply(Object t) {
            if (t == null) {
                return null;
            }
            return expression.getValue(evaluationContext, t);
        }
    }
}
