package com.hp.biz.logger.function;

/**
 * @author hp
 */
public interface IBizLoggerFunctionResolver extends IBizLoggerFunctionRegistrar {

    String resolveFunctions(String expression);
}
