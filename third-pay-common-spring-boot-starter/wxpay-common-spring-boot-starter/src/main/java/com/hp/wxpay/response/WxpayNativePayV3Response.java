package com.hp.wxpay.response;

import com.fasterxml.jackson.annotation.JsonAlias;

/**
 * @author HP 2022/11/23
 */
public class WxpayNativePayV3Response extends WxpayResponse {
    private static final long serialVersionUID = -1627868049929615856L;

    @JsonAlias("code_url")
    private String codeUrl;
}
