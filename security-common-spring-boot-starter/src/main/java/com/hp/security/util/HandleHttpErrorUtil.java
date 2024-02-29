package com.hp.security.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.security.exception.CustomAuthenticationException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
            authResponse.setStatus(cusException.getCode());
            authResponse.setMessage(cusException.getMessage());
        } else {
            authResponse.setStatus(AUTH_ERROR_CODE);
            authResponse.setMessage(e.getMessage());
        }
        ObjectMapper objectMapper = new ObjectMapper();
        response.getOutputStream().write(objectMapper.writeValueAsBytes(authResponse));
    }

    @Data
    static class AuthResponse implements Serializable {
        private static final long serialVersionUID = -3474646849529247442L;
        private Integer status;
        private String message;
        private Object data;
    }
}
