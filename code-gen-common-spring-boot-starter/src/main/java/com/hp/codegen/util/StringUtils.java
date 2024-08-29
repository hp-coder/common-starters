package com.hp.codegen.util;

import cn.hutool.core.util.ArrayUtil;
import com.google.common.base.CaseFormat;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Objects;

/**
 * @author hp
 */
@UtilityClass
public class StringUtils {

    public static String camel(String source) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, source);
    }

    public static boolean containsNull(List<String> list) {
        List<String> nullList = list.stream().filter(Objects::isNull).toList();
        return !nullList.isEmpty();
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
        if (value == null || value.isEmpty()) {
            return false;
        }
        return ArrayUtil.isAllNotEmpty((Object) list);
    }
}
