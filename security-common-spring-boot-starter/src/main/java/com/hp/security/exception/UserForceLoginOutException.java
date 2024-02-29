package com.hp.security.exception;

import com.hp.security.config.AuthErrorMsg;

/**
 * @author hp
 */
public class UserForceLoginOutException extends AbstractAuthenticationException {
    private static final long serialVersionUID = -1678391978181637002L;

    public UserForceLoginOutException() {
        super(AuthErrorMsg.forceLoginOut);
    }

    public UserForceLoginOutException(String msg) {
        super(AuthErrorMsg.forceLoginOut.getCode(), msg);
    }

    public UserForceLoginOutException(Integer code, String msg) {
        super(code, msg);
    }
}
