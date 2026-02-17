package com.hp.joininmemory.exception;

import lombok.Getter;

import java.io.Serial;

/**
 *
 * @version 1.0.0
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026/1/6
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
