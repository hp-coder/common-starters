package com.hp.lazyloader.factory;

import cn.hutool.core.util.ClassUtil;
import org.springframework.aop.framework.ProxyFactory;

import java.lang.reflect.Modifier;
import java.util.Objects;

/**
 * @author hp
 */
public class AbstractLazyLoaderProxyFactory implements LazyLoaderProxyFactory {

    protected final LazyLoaderInterceptorFactory lazyLoaderInterceptorFactory;

    public AbstractLazyLoaderProxyFactory(LazyLoaderInterceptorFactory lazyLoaderInterceptorFactory) {
        this.lazyLoaderInterceptorFactory = lazyLoaderInterceptorFactory;
    }

    @Override
    public <T> T createFor(T t) {
        if (Objects.isNull(t)) {
            return null;
        }
        @SuppressWarnings("unchecked")
        final Class<T> klass = (Class<T>) t.getClass();
        if (klass.isPrimitive() || ClassUtil.isPrimitiveWrapper(klass)) {
            return t;
        }
        if (Modifier.isFinal(klass.getModifiers())) {
            return t;
        }
        return createProxy(klass, t);
    }

    protected <T> T createProxy(Class<T> klass, T t) {
        final ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(t);
        proxyFactory.addAdvice(this.lazyLoaderInterceptorFactory.createFor(klass, t));
        @SuppressWarnings("unchecked")
        final T proxy = (T) proxyFactory.getProxy();
        return proxy;
    }
}
