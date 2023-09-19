package com.luban.excel.head;

/**
 * @author hp
 * @date 2022/11/7
 */
public interface HeadGenerator {
    HeadMetaData head(Class<?> clazz);
}
