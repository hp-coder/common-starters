package com.luban.security.base;

import com.google.common.base.Strings;
import com.luban.security.config.SecurityCommonProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * token 拦截器，加入上下文参数 user-agent ，也可以加入其它的扩展
 *
 * @author hp
 */
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    public static final String HEADER_TOKEN_NAME = "token";
    public static final String USER_AGENT = HttpHeaders.USER_AGENT;

    private final List<String> unAuthLoginUrls;

    public JwtAuthenticationTokenFilter(SecurityCommonProperties properties) {
        this.unAuthLoginUrls = properties.getUnAuthLoginUrls();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain) throws ServletException, IOException {
        String authorization = request.getHeader(HEADER_TOKEN_NAME);
        String userAgent = request.getHeader(USER_AGENT);
        AtomicBoolean skip = new AtomicBoolean(false);
        if (!CollectionUtils.isEmpty(unAuthLoginUrls)) {
            unAuthLoginUrls.stream().map(AntPathRequestMatcher::new).filter(matcher -> matcher.matches(request)).findAny().ifPresent(i -> skip.set(true));
        }
        if (!Strings.isNullOrEmpty(authorization) && !skip.get()) {
            JwtAuthToken token = new JwtAuthToken(authorization, userAgent);
            SecurityContextHolder.getContext().setAuthentication(token);
        }
        chain.doFilter(request, response);
    }
}
