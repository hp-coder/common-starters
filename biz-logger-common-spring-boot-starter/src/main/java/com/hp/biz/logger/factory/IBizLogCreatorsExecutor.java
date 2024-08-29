
package com.hp.biz.logger.factory;

import com.hp.biz.logger.model.BizLogDTO;
import com.hp.biz.logger.model.MethodInvocationWrapper;

import java.util.Collection;

/**
 * @author hp
 */
public interface IBizLogCreatorsExecutor {

    Collection<BizLogDTO> execute(MethodInvocationWrapper invocationWrapper, boolean preInvocation);

}
