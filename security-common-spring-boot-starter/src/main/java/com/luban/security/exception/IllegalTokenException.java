package com.luban.security.exception;

import com.luban.security.config.AuthErrorMsg;

/**
 * @author hp
 */
public class IllegalTokenException extends AbstractAuthenticationException {
    private static final long serialVersionUID = -6570426781003064437L;

    public IllegalTokenException() {
        super(AuthErrorMsg.tokenIllegal);
    }

    public IllegalTokenException(String msg) {
        super(AuthErrorMsg.tokenIllegal.getCode(), msg);
    }

    public IllegalTokenException(Integer code, String msg) {
        super(code, msg);
    }
}
