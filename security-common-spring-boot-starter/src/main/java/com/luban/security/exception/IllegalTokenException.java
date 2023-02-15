package com.luban.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author hp
 */
public class IllegalTokenException extends AuthenticationException {
    private static final long serialVersionUID = -6570426781003064437L;

    public IllegalTokenException(String msg) {
        super(msg);
    }
}
