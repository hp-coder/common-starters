package com.hp.joininmemory.exception;

import lombok.Getter;

/**
 * @author hp
 */
@Getter
public class JoinException extends RuntimeException {

    private static final long serialVersionUID = -2290173202494060253L;

    private final JoinErrorCode code;

    public JoinException(JoinErrorCode code, Throwable throwable) {
        super(code.toString() + "=" + throwable.getMessage(), throwable);
        this.code = code;
    }
}
