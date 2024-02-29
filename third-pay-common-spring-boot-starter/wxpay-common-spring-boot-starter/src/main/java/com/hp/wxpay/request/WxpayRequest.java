package com.hp.wxpay.request;

import com.hp.wxpay.domain.WxpayObject;
import com.hp.wxpay.response.WxpayResponse;
import org.springframework.http.HttpMethod;

/**
 * @author hp
 */
public interface WxpayRequest<T extends WxpayResponse> {

    String getApiMethodName();

    String getApiVersion();

    HttpMethod getHttpMethod();

    String getFormat();

    void setApiVersion(String apiVersion);

    String getNotifyUrl();

    void setNotifyUrl(String notifyUrl);

    String getReturnUrl();

    void setReturnUrl(String returnUrl);

    Class<T> getResponseClass();

    boolean isNeedEncrypt();

    void setNeedEncrypt(boolean needEncrypt);

    WxpayObject getBizModel();

    void setBizModel(WxpayObject bizModel);
}
