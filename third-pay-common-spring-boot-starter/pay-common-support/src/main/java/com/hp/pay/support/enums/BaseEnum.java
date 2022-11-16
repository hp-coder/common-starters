package com.hp.pay.support.enums;

import java.util.Objects;

public interface BaseEnum<T extends Enum<T> & BaseEnum<T>> {

    /**
     * 获取code码存入数据库
     *
     * @return 获取编码
     */
    String getCode();

    /**
     * 获取编码名称，便于维护
     *
     * @return 获取编码名称
     */
    String getName();

    /**
     * 根据code码获取枚举
     *
     * @param cls  enum class
     * @param code enum code
     * @return get enum
     */
    static <T extends Enum<T> & BaseEnum<T>> T parseByCode(Class<T> cls, String code) {
        for (T t : cls.getEnumConstants()) {
            if (Objects.equals(t.getCode(), code)) {
                return t;
            }
        }
        return null;
    }
}