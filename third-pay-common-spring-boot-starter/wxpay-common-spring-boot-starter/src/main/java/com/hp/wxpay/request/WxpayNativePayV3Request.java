package com.hp.wxpay.request;

import com.hp.wxpay.domain.WxpayObject;
import com.hp.wxpay.response.WxpayNativePayV3Response;
import com.hp.wxpay.support.WxpayConstant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpMethod;

/**
 * @author hp
 */
@Getter
@Setter
public class WxpayNativePayV3Request implements WxpayRequest<WxpayNativePayV3Response> {

    private String apiVersion = "v3";
    private String notifyUrl;
    private String returnUrl;
    private String format = WxpayConstant.FORMAT_JSON;
    private HttpMethod httpMethod = HttpMethod.POST;
    private boolean needEncrypt = false;
    private WxpayObject bizModel = null;

    @Override
    public String getApiMethodName() {
        return "/v3/pay/transactions/native";
    }

    @Override
    public Class<WxpayNativePayV3Response> getResponseClass() {
        return WxpayNativePayV3Response.class;
    }
}
