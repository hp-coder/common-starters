package com.hp.excel.model;

import java.util.Collection;
import java.util.Map;

/**
 * @author hp
 */
public interface ExcelSelectionProvider<PARENT, CHILDREN extends Collection<?>> {

    Collection<PARENT> findAllSelectionByConditions(Object parameter);

    Map<PARENT, CHILDREN> findAllCascadeByConditions(Object parameter);

}
