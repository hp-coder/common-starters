package com.luban.security.base;

import lombok.Data;

/**
 * @author hp
 */
@Data
public class LoginSuccessResponse {

  private String token;
  private String username;
}
