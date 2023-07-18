package com.hp.security.base;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author hp
 */
public abstract class AbstractJwtAuthToken implements Authentication {
    private static final long serialVersionUID = 3307781535363682674L;
    protected final String token;
    @Setter
    @Getter
    protected String userAgent;

    public AbstractJwtAuthToken(String token) {
        this.token = token;
    }
    public AbstractJwtAuthToken(String token, String userAgent) {
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
