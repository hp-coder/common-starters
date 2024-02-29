package com.hp.wxpay.response;

import com.fasterxml.jackson.annotation.JsonAlias;

/**
 * @author hp
 */
public class WxpayNativePayV3Response extends WxpayResponse {
    private static final long serialVersionUID = -1627868049929615856L;

    @JsonAlias("code_url")
    private String codeUrl;
}
