package com.hp.excel.head;

/**
 * @author HP
 * @date 2022/11/7
 */
public interface HeadGenerator {
    HeadMetaData head(Class<?> clazz);
}
