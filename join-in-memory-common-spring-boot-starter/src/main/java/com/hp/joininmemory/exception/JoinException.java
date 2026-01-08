package com.hp.joininmemory.exception;

import lombok.Getter;

import java.io.Serial;

/**
 * @author <a href="mailto:max_verstrappon@outlook.com">HuPeng</a>
 */
@Getter
public class JoinException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -5670297424930807844L;

    private final JoinErrorCode code;

    public JoinException(JoinErrorCode code, Throwable throwable) {
        super(code.toString() + "=" + throwable.getMessage(), throwable);
        this.code = code;
    }
}
