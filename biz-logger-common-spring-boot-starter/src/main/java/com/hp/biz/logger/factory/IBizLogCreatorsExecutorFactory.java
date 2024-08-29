
package com.hp.biz.logger.factory;

import java.lang.reflect.Method;

/**
 * @author hp
 */
public interface IBizLogCreatorsExecutorFactory {

    IBizLogCreatorsExecutor createFor(Class<?> targetClass, Method method);

}
