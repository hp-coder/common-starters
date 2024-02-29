package com.hp.security.base;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author hp
 */
public abstract class BaseUsernamePasswordToken extends AbstractAuthenticationToken {

  private static final long serialVersionUID = 4869510722869347201L;
  @Getter
  private final String username;
  @Getter
  private final String password;


  public BaseUsernamePasswordToken(Collection<? extends GrantedAuthority> authorities,
      String username,
      String password) {
    super(authorities);
    this.username = username;
    this.password = password;
  }

  @Override
  public Object getCredentials() {
    return this.password;
  }

  @Override
  public Object getPrincipal() {
    return this.username;
  }
}
