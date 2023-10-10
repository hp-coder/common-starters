package com.luban.joininmemory.exception;

/**
 * @author hp
 */
public class JoinException extends RuntimeException {

    public JoinException() {
    }

    public JoinException(JoinErrorCode code, Throwable throwable) {
        super(code.toString(), throwable);
    }

    public JoinException(String message) {
        super(message);
    }

    public JoinException(String message, Throwable cause) {
        super(message, cause);
    }

    public JoinException(Throwable cause) {
        super(cause);
    }

    public JoinException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
