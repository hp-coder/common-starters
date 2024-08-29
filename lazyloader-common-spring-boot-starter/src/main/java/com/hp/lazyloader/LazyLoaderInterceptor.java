package com.hp.lazyloader;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Nonnull;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.ProxyMethodInvocation;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author hp
 */
public class LazyLoaderInterceptor implements MethodInterceptor, InvocationHandler {

    private final Map<String, PropertyLazyLoader> lazyLoaderCache;
    private final Object target;

    public LazyLoaderInterceptor(
            Map<String, PropertyLazyLoader> lazyLoaderCache,
            Object target
    ) {
        this.target = target;
        this.lazyLoaderCache = lazyLoaderCache;
    }

    @Nullable
    @Override
    public Object invoke(@Nonnull MethodInvocation methodInvocation) throws Throwable {
        if (methodInvocation instanceof ProxyMethodInvocation proxyMethodInvocation) {
            return invoke(proxyMethodInvocation.getProxy(), proxyMethodInvocation.getMethod(), proxyMethodInvocation.getArguments());
        }
        return invoke(methodInvocation.getThis(), methodInvocation.getMethod(), methodInvocation.getArguments());
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        if (isGetter(method)) {
            final String fieldName = StrUtil.lowerFirst(method.getName().replace("get", StrUtil.EMPTY));
            final PropertyLazyLoader propertyLazyLoader = this.lazyLoaderCache.get(fieldName);
            if (propertyLazyLoader != null) {
                Object data = method.invoke(target, objects);
                if (data != null) {
                    return data;
                }
                data = propertyLazyLoader.getSpELGetter().apply(o);
                if (data != null) {
                    ReflectUtil.setFieldValue(target, propertyLazyLoader.getField(), data);
                }
                return data;
            } else {
                method.invoke(target, objects);
            }
        }
        return method.invoke(target, objects);
    }

    private static boolean isGetter(Method method) {
        if (null == method) {
            return false;
        }
        if (method.getParameterCount() > 0) {
            return false;
        }
        final String name = method.getName();
        // 跳过getClass这个特殊方法
        if ("getClass".equals(name)) {
            return false;
        }
        return name.startsWith("get") || name.startsWith("is");
    }
}
