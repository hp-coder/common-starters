package com.hp.wxpay.response;

import lombok.Getter;
import lombok.Setter;

/**
 * @author hp
 */
@Getter
@Setter
public class WxpayNativePayV2Response extends WxpayResponse {
    private static final long serialVersionUID = -1627868049929615856L;

    private String appid;
    private String mch_id;
    private String device_info;
    private String nonce_str;
    private String sign;
    private String result_code;
    private String err_code;
    private String err_code_des;

    private String trade_type;
    private String prepay_id;
    private String code_url;
    }
