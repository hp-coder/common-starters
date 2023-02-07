package com.hp.wxpay;

import lombok.Getter;

/**
 * @author HP 2022/11/22
 */
@Getter
public class WxpayApiException extends Exception {

    private static final long serialVersionUID = -4865906324077609628L;
    private String errCode;
    private String errMsg;

    public WxpayApiException() {
        super();
    }

    public WxpayApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public WxpayApiException(String message) {
        super(message);
    }

    public WxpayApiException(Throwable cause) {
        super(cause);
    }

    public WxpayApiException(String errCode, String errMsg) {
        super(errCode + ":" + errMsg);
        this.errCode = errCode;
        this.errMsg = errMsg;
    }
}
