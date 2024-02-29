package com.hp.codegen.util;

import cn.hutool.core.util.ArrayUtil;
import com.google.common.base.CaseFormat;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author hp
 */
public final class StringUtils {

    private StringUtils() {
    }

    public static String camel(String source) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, source);
    }

    public static boolean containsNull(List<String> list) {
        List<String> nullList = list.stream().filter(Objects::isNull).collect(Collectors.toList());
        return nullList.size() > 0;
    }

    public static boolean containsNull(String value, String... list) {
        if (value == null) {
            return true;
        }
        return ArrayUtil.hasNull(list);
    }

    public static String uncapitalize(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    public static boolean notEmpty(String value, String... list) {
        if (value == null || value.length() == 0) {
            return false;
        }
        return ArrayUtil.isAllNotEmpty(list);
    }
}
