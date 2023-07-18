package com.hp.security.base;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

/**
 * @author hp
 */
public class LoginSuccessToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 324521504234222456L;
    private final String token;
    @Getter
    private final String username;

    public LoginSuccessToken(String token,String name) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.token = token;
        this.username = name;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }
}
