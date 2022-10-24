package com.hp.security.base;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author HP
 * @date 2022/10/21
 */
public abstract class BaseUsernamePasswordToken extends AbstractAuthenticationToken {

    private final String username;
    private final String password;

    public BaseUsernamePasswordToken(String username, String password, Collection<? extends GrantedAuthority> authorities) {
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
