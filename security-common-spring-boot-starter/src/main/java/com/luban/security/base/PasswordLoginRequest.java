package com.luban.security.base;

import lombok.Data;

/**
 * @author hp
 */
@Data
public class PasswordLoginRequest {

  private String username;
  private String password;
  private boolean forceLogin;
}
