package com.hp.security.exception;

import com.hp.security.config.AuthErrorMsg;

/**
 * @author hp
 */
public class TokenExpiredException extends AbstractAuthenticationException {
    private static final long serialVersionUID = 772003740293283605L;

    public TokenExpiredException() {
        super(AuthErrorMsg.tokenExpired);
    }

    public TokenExpiredException(String msg) {
        super(AuthErrorMsg.tokenExpired.getCode(), msg);
    }

    public TokenExpiredException(Integer code, String msg) {
        super(code, msg);
    }
}
