package com.luban.common.base.model;

import java.util.HashMap;
import java.util.Objects;

/**
 * @author hp
 */
public class Returns extends HashMap<String, Object> {
    public static final String CODE_TAG = "code";
    public static final String MSG_TAG = "message";
    public static final String DATA_TAG = "data";
    private static final long serialVersionUID = 1L;

    public Returns() {
    }

    public Returns(int code, String msg) {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
    }

    public Returns(int code, String msg, Object data) {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
        if (data != null) {
            super.put(DATA_TAG, data);
        }
    }

    public static Returns success() {
        return Returns.success("操作成功");
    }

    public static Returns success(Object data) {
        return Returns.success("操作成功", data);
    }

    public static Returns success(String msg) {
        return Returns.success(msg, null);
    }

    public static Returns success(String msg, Object data) {
        return new Returns(200, msg, data);
    }

    public static Returns error() {
        return Returns.error("操作失败");
    }

    public static Returns error(String msg) {
        return Returns.error(msg, null);
    }

    public static Returns error(Object data) {
        return Returns.error("操作失败", data);
    }

    public static Returns error(String msg, Object data) {
        return new Returns(500, msg, data);
    }

    public static Returns error(int code, String msg) {
        return new Returns(code, msg, null);
    }

    public boolean isSuccess() {
        return !isError();
    }

    public boolean isError() {
        return !Objects.equals(200, this.get(CODE_TAG));
    }

    @Override
    public Returns put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
