package com.luban.joininmemory.exception;

import lombok.Getter;

/**
 * @author hp
 */
public class JoinException extends RuntimeException {

    private static final long serialVersionUID = -2290173202494060253L;

    @Getter
    private final JoinErrorCode code;

    public JoinException(JoinErrorCode code) {
        super(code.getName());
        this.code = code;
    }

    public JoinException(JoinErrorCode code, Throwable throwable) {
        super(code.toString(), throwable);
        this.code = code;
    }
}
