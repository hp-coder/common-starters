package com.hp.aspose.exception;

/**
 * @author hp 2023/4/20
 */
public class AsposeException extends RuntimeException{

    public AsposeException() {
    }

    public AsposeException(String message) {
        super(message);
    }

    public AsposeException(String message, Throwable cause) {
        super(message, cause);
    }

    public AsposeException(Throwable cause) {
        super(cause);
    }

    public AsposeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
