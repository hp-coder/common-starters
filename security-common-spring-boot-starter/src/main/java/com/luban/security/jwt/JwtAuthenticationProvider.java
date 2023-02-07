package com.luban.security.jwt;

import com.luban.security.base.BaseAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * @author HP
 * @date 2022/10/21
 */
public class JwtAuthenticationProvider extends BaseAuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return false;
    }
}
