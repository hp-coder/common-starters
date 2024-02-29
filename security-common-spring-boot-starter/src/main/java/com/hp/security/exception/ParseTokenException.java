package com.hp.security.exception;

import lombok.Getter;

/**
 * @author hp
 */
@Getter
public class ParseTokenException extends RuntimeException {

    private static final long serialVersionUID = 8656006470182321029L;

    public ParseTokenException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
    private final Integer code;
    private final String msg;
}
