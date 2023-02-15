package com.luban.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author hp
 */
public class UserForceLoginOutException extends AuthenticationException {
    private static final long serialVersionUID = -1678391978181637002L;

    public UserForceLoginOutException(String msg) {
        super(msg);
    }
}
