package com.luban.excel.head;

import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * @author HP
 * @date 2022/11/7
 */
@Data
public class HeadMetaData {
    private List<List<String>> head;
    private Set<String> ignoreHeadFields;
}
