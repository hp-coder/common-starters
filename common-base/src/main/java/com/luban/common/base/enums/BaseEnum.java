package com.luban.common.base.enums;

public interface BaseEnum<T extends Enum<T> & BaseEnum<T>> {

    Integer getCode();

    String getName();

    static <T extends Enum<T> & BaseEnum<T>> T parseByCode(Class<T> cls, Integer code) {
        if (code == null) {
            return null;
        }
        for (T t : cls.getEnumConstants()) {
            if (t.getCode().intValue() == code.intValue()) {
                return t;
            }
        }
        return null;
    }

    static <T extends Enum<T> & BaseEnum<T>> T parseByName(Class<T> cls, String name) {
        if (name == null || name.length() == 0) {
            return null;
        }
        for (T t : cls.getEnumConstants()) {
            if (t.getName().equals(name)) {
                return t;
            }
        }
        return null;
    }
}
