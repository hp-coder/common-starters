package com.hp.security.base;

import com.google.common.base.Strings;
import com.hp.security.config.SecurityCommonProperties;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * token 拦截器，加入上下文参数 user-agent ，也可以加入其它的扩展
 *
 * @author hp
 */
public abstract class AbstractJwtAuthenticationTokenFilter extends OncePerRequestFilter {
    private final List<String> unAuthLoginUrls;
    protected String token;

    public AbstractJwtAuthenticationTokenFilter(SecurityCommonProperties properties) {
        this.unAuthLoginUrls = properties.getUnAuthLoginUrls();
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain) throws ServletException, IOException {
        if (!Strings.isNullOrEmpty(token = token(request)) && !skip(request)) {
            SecurityContextHolder.getContext().setAuthentication(authentication(request));
        }
        chain.doFilter(request, response);
    }

    protected abstract String token(@NonNull HttpServletRequest request);

    protected boolean skip(@NonNull HttpServletRequest request) {
        AtomicBoolean skip = new AtomicBoolean(false);
        if (!CollectionUtils.isEmpty(this.unAuthLoginUrls)) {
            unAuthLoginUrls.stream().map(AntPathRequestMatcher::new).filter(matcher -> matcher.matches(request)).findAny().ifPresent(i -> skip.set(true));
        }
        return skip.get();
    }

    protected abstract AbstractJwtAuthToken authentication(@NonNull HttpServletRequest request);
}
