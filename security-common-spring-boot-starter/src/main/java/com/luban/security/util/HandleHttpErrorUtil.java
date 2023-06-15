package com.luban.security.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luban.security.exception.CustomAuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;

import java.io.Serializable;

/**
 * @author hp
 */
@Slf4j
public final class HandleHttpErrorUtil {

    private final static Integer AUTH_ERROR_CODE = HttpStatus.UNAUTHORIZED.value();

    private HandleHttpErrorUtil() {
    }

    public static void handleHttpError(HttpServletRequest request, HttpServletResponse response,
                                       AuthenticationException e) throws Exception {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        AuthResponse authResponse = new AuthResponse();
        if (CustomAuthenticationException.class.isAssignableFrom(e.getClass())) {
            CustomAuthenticationException cusException = (CustomAuthenticationException) e;
            authResponse.setCode(cusException.getCode());
            authResponse.setMsg(cusException.getMessage());
        } else {
            authResponse.setCode(AUTH_ERROR_CODE);
            authResponse.setMsg(e.getMessage());
        }
        ObjectMapper objectMapper = new ObjectMapper();
        response.getOutputStream().write(objectMapper.writeValueAsBytes(authResponse));
    }

    @Data
    static class AuthResponse implements Serializable {
        private static final long serialVersionUID = -3474646849529247442L;
        private Integer code;
        private String msg;
        private Object result;
    }
}
