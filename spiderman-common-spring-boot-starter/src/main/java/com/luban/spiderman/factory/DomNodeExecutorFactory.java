package com.luban.spiderman.factory;

import java.util.List;

/**
 * @author hp
 */
public interface DomNodeExecutorFactory {

    <TYPE> List<DomNodeExecutor<TYPE>> createForType(Class<TYPE> clazz);
}
