package com.hp.extension.executor;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author HP
 * @date 2022/10/19
 */
@Component
public class ServiceSelectorExecutor extends AbstractServiceExecutor implements ApplicationContextAware, SmartInitializingSingleton {
    private ApplicationContext applicationContext;
    private final Map<String, Object> extBeanHolder = new ConcurrentHashMap<>();

    @Override
    protected <S> S selectService(BizScene scene, Class<S> service) {
        final Object bean = extBeanHolder.get(scene.bizId());
        Assert.notNull(bean, () -> {
            throw new RuntimeException(" Service Not Found ");
        });
        return (S) bean;
    }

    @Override
    public void afterSingletonsInstantiated() {
        final Map<String, Object> extensionServices = this.applicationContext.getBeansWithAnnotation(Extension.class);
        extensionServices.forEach((k, v) -> {
            Class<?> aClass = v.getClass();
            if (AopUtils.isAopProxy(v)) {
                aClass = ClassUtils.getUserClass(v);
            }
            final Extension extension = AnnotationUtils.findAnnotation(aClass, Extension.class);
            final Object exist = extBeanHolder.put(extension.bizId(), v);
            Assert.isNull(exist, () -> {
                throw new RuntimeException(" BizId Already Existed ");
            });
        });

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
