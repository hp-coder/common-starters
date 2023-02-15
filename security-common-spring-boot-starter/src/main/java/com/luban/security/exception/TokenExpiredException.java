package com.luban.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author hp
 */
public class TokenExpiredException extends AuthenticationException {
    private static final long serialVersionUID = 772003740293283605L;

    public TokenExpiredException(String msg) {
        super(msg);
    }
}
