package com.hp.security.exception;

import com.hp.security.config.AuthErrorMsg;

/**
 * @author hp
 */
public class MethodNotSupportException extends AbstractAuthenticationException {
    private static final long serialVersionUID = 1472175773690915274L;

    public MethodNotSupportException() {
        super(AuthErrorMsg.methodNotSupport);
    }
    public MethodNotSupportException(String msg) {
        super(AuthErrorMsg.methodNotSupport.getCode(), msg);
    }
    public MethodNotSupportException(Integer code, String msg) {
        super(code, msg);
    }
}
