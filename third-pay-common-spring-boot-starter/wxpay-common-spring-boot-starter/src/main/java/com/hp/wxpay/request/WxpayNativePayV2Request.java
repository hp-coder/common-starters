package com.hp.wxpay.request;

import com.hp.wxpay.domain.WxpayObject;
import com.hp.wxpay.response.WxpayNativePayV2Response;
import com.hp.wxpay.support.WxpayConstant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpMethod;

/**
 * @author hp
 */
@Getter
@Setter
public class WxpayNativePayV2Request implements WxpayRequest<WxpayNativePayV2Response> {

    private String apiVersion = "v2";
    private String notifyUrl;
    private String returnUrl;
    private String format = WxpayConstant.FORMAT_XML;
    private HttpMethod httpMethod = HttpMethod.POST;
    private boolean needEncrypt = false;
    private WxpayObject bizModel = null;

    @Override
    public String getApiMethodName() {
        return "/pay/unifiedorder";
    }

    @Override
    public Class<WxpayNativePayV2Response> getResponseClass() {
        return WxpayNativePayV2Response.class;
    }
}
