package com.luban.wxpay.request;

import com.luban.wxpay.domain.WxpayObject;
import com.luban.wxpay.response.WxpayResponse;
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
