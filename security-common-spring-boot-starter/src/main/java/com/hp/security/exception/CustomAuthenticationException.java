package com.hp.security.exception;

/**
 * @author hp
 */
public class CustomAuthenticationException extends AbstractAuthenticationException {

    private static final long serialVersionUID = 8376647653520119271L;
    public CustomAuthenticationException(Integer code, String msg) {
        super(code, msg);
    }
}
