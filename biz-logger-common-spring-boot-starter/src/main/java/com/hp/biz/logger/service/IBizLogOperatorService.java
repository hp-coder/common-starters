package com.hp.biz.logger.service;

import com.hp.biz.logger.model.BizLogOperator;

/**
 * @author hp
 */
@FunctionalInterface
public interface IBizLogOperatorService {

    BizLogOperator get();

}
