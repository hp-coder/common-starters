package com.luban.common.base.exception;

import com.luban.common.base.enums.BaseEnum;

public class BusinessException extends RuntimeException {
    private final BaseEnum msg;
    private Object data;

    public BusinessException(BaseEnum msg) {
        super(msg.getName());
        this.msg = msg;
    }

    public BusinessException(BaseEnum msg, Object data) {
        super(msg.getName());
        this.msg = msg;
        this.data = data;
    }

    public BaseEnum getMsg() {
        return this.msg;
    }

    public Object getData() {
        return this.data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
