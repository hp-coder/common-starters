package com.luban.security.exception;

import com.luban.security.config.AuthErrorMsg;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

/**
 * @author hp 2023/2/17
 */
public abstract class AbstractAuthenticationException extends AuthenticationException {

    private static final long serialVersionUID = 1223726291988441150L;
    @Getter
    protected final Integer code;

    public AbstractAuthenticationException(Integer code, String msg, Throwable cause) {
        super(msg, cause);
        this.code = code;
    }

    public AbstractAuthenticationException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }

    public AbstractAuthenticationException(AuthErrorMsg error) {
        super(error.getName());
        this.code = error.getCode();
    }
}
