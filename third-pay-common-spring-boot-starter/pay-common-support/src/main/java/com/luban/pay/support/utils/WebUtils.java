package com.luban.pay.support.utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * @author hp
 */
public final class WebUtils {
    public static HttpServletRequest getRequest() {
        return Optional.of((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .map(ServletRequestAttributes::getRequest)
                .orElse(null);
    }

    public static HttpServletResponse getResponse() {
        return Optional.of((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .map(ServletRequestAttributes::getResponse)
                .orElse(null);
    }
}
