package com.luban.spiderman.factory.support;

import cn.hutool.core.util.ArrayUtil;
import com.google.common.collect.Maps;
import com.luban.spiderman.annotations.SpiderConfig;
import com.luban.spiderman.annotations.SpiderHeader;
import com.luban.spiderman.annotations.SpiderPage;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author hp
 */
@Slf4j
public class SpiderConfigBasedConnectionFactory extends AbstractAnnotationBasedConnectionFactory<SpiderConfig> {

    private final SpelExpressionParser parser = new SpelExpressionParser();
    private final TemplateParserContext templateParserContext = new TemplateParserContext();
    private final BeanResolver beanResolver;

    public SpiderConfigBasedConnectionFactory(BeanResolver beanResolver) {
        super(SpiderConfig.class);
        this.beanResolver = beanResolver;
    }

    @Override
    protected <SOURCE> Collection<? extends String> urls(SpiderConfig annotation, SOURCE source) {
        final List<String> baseUrls = generateBaseUrl(annotation, source);
        if (!annotation.page().required() || !annotation.method().equals(Connection.Method.GET)) {
            return baseUrls;
        }
        return generateUrlWithPage(annotation, baseUrls);
    }

    private Collection<? extends String> generateUrlWithPage(SpiderConfig annotation, List<String> baseUrls) {
        final String template = "%s?%s=%s&%s=%s";
        final SpiderPage page = annotation.page();
        return baseUrls.stream()
                .flatMap(url -> Stream.iterate(1, i -> i + 1)
                        .limit(page.maximumPage())
                        .map(j -> {
                            final String format = String.format(template, url, page.pageParam(), j, page.sizeParam(), page.maximumSize());
                            log.debug("expanded urls: {}", format);
                            return format;
                        })
                )
                .collect(Collectors.toList());
    }

    private <SOURCE> List<String> generateBaseUrl(SpiderConfig annotation, SOURCE source) {
        final Expression expression = parser.parseExpression(annotation.website(), templateParserContext);
        final StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
        evaluationContext.setBeanResolver(beanResolver);
        if (source != null && Collection.class.isAssignableFrom(source.getClass())) {
            final Collection<?> collection = (Collection<?>) source;
            return collection.stream()
                    .map(i -> {
                        evaluationContext.setRootObject(i);
                        return (String) expression.getValue(evaluationContext);
                    }).collect(Collectors.toList());
        } else {
            evaluationContext.setRootObject(source);
            final String value = (String) expression.getValue(evaluationContext);
            return Collections.singletonList(value);
        }
    }

    @Override
    protected Connection.Method method(SpiderConfig annotation) {
        return annotation.method();
    }

    @Override
    protected Map<String, String> headers(SpiderConfig annotation) {
        final SpiderHeader[] spiderHeaders = annotation.headers();
        if (ArrayUtil.isEmpty(spiderHeaders)) {
            return null;
        }
        final Map<String, String> headers = Maps.newHashMap();
        Arrays.stream(spiderHeaders).forEach(head -> headers.put(head.header(), head.value()));
        return headers;
    }
}
