package com.hp.biz.logger.exception;

import com.hp.common.base.enums.BaseEnum;
import lombok.Getter;

import java.io.Serial;

/**
 * @author hp
 */
@Getter
public class BizLoggerException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1726443815519321738L;

    private BaseEnum<?, ?> errorCode;

    private Throwable throwable;

    public BizLoggerException(String msg) {
        super(msg);
    }

    public BizLoggerException(String msg, Throwable chain) {
        super(msg);
        this.throwable = chain;
    }

    public BizLoggerException(BaseEnum<?,?> errorCode, Throwable chain) {
        super(errorCode.getName());
        this.errorCode = errorCode;
        this.throwable = chain;
    }
}
