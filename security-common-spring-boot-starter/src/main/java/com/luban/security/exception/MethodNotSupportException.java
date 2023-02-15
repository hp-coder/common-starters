package com.luban.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author hp
 */
public class MethodNotSupportException extends AuthenticationException {
    private static final long serialVersionUID = 1472175773690915274L;

    public MethodNotSupportException(String msg) {
        super(msg);
    }
}
