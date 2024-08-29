package com.hp.biz.logger.factory;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author hp
 */
public interface IBizLogCreatorFactory {

    List<IBizLogCreator> createFor(Class<?> targetClass, Method method);
}
