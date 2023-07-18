package com.hp.security.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.security.config.AuthErrorMsg;
import com.hp.security.exception.MethodNotSupportException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

/**
 * 通用登录处理器
 *
 * @author hp
 */
public abstract class BaseLoginProcessFilter extends AbstractAuthenticationProcessingFilter {
    @Getter
    private final AuthenticationSuccessHandler successHandler;
    @Getter
    private final AuthenticationFailureHandler failureHandler;
    @Getter
    private final ObjectMapper mapper;

    protected BaseLoginProcessFilter(
            String defaultFilterProcessesUrl,
            AuthenticationSuccessHandler successHandler,
            AuthenticationFailureHandler failureHandler,
            ObjectMapper mapper
    ) {
        super(defaultFilterProcessesUrl);
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
        this.mapper = mapper;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        successHandler.onAuthenticationSuccess(request, response, authResult);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        failureHandler.onAuthenticationFailure(request, response, failed);
    }

    public void checkMethod(HttpServletRequest request) {
        if (!HttpMethod.POST.name().equals(request.getMethod())) {
            throw new MethodNotSupportException(AuthErrorMsg.methodNotSupport.getName());
        }
    }
}
