package com.luban.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author hp
 */
public class CustomAuthenticationException extends AuthenticationException {

  private static final long serialVersionUID = 8376647653520119271L;
  private final Integer code;

  public CustomAuthenticationException(Integer code, String msg) {
    super(msg);
    this.code = code;
  }

  public Integer getCode() {
    return code;
  }

}
