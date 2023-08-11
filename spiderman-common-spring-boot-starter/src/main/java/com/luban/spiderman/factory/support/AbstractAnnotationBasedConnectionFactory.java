package com.luban.spiderman.factory.support;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.luban.spiderman.factory.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

import static com.luban.common.base.utils.ParamUtils.Maps;
import static com.luban.common.base.utils.ParamUtils.Strings;
import static org.jsoup.Connection.Method;

/**
 * @author hp
 */
@Slf4j
public abstract class AbstractAnnotationBasedConnectionFactory<A extends Annotation> implements ConnectionFactory {
    private final Class<A> annotationClass;

    public AbstractAnnotationBasedConnectionFactory(Class<A> annotationClass) {
        this.annotationClass = annotationClass;
    }

    @Deprecated
    @Override
    public <TYPE> List<Connection> createFor(Class<TYPE> data) {
        final A annotation = AnnotatedElementUtils.getMergedAnnotation(data, annotationClass);
        return generateConnections(annotation, null);
    }

    @Override
    public <TYPE, SOURCE> List<Connection> createFor(Class<TYPE> data, SOURCE source) {
        final A annotation = AnnotatedElementUtils.getMergedAnnotation(data, annotationClass);
        return generateConnections(annotation, source);
    }

    private <SOURCE> List<Connection> generateConnections(A annotation, SOURCE source) {
        if (annotation == null){
            return Collections.emptyList();
        }
        final Collection<? extends String> urls = urls(annotation, source);
        if (CollUtil.isEmpty(urls)) {
            return Collections.emptyList();
        }
        return urls.stream()
                .map(url -> {
                    final Connection connect = Jsoup.connect(url);
                    Optional.ofNullable(method(annotation)).ifPresent(connect::method);
                    Maps.ofNullable(headers(annotation)).ifPresent(connect::headers);
                    Maps.ofNullable(requestData(annotation)).ifPresent(connect::data);
                    Strings.ofNullable(requestBody(annotation)).ifPresent(connect::requestBody);
                    return connect;
                })
                .collect(Collectors.toList());
    }

    protected Method method(A annotation) {
        return Method.GET;
    }

    protected abstract <SOURCE> Collection<? extends String> urls(A annotation, SOURCE source);

    protected abstract Map<String, String> headers(A annotation);

    protected Map<String, String> requestData(A annotation) {
        return Collections.emptyMap();
    }

    protected String requestBody(A annotation) {
        return StrUtil.EMPTY;
    }
}
