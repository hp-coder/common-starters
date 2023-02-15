package com.luban.security.base;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author hp
 */
public class JwtAuthToken implements Authentication {
    private static final long serialVersionUID = 3307781535363682674L;
    private final String token;
    @Setter
    @Getter
    private String userAgent;

    public JwtAuthToken(String token) {
        this.token = token;
    }
    public JwtAuthToken(String token,String userAgent) {
        this.token = token;
        this.userAgent = userAgent;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        return false;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    }

    @Override
    public String getName() {
        return null;
    }
}
