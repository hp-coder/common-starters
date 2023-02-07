package com.hp.wxpay;

import com.hp.wxpay.request.WxpayRequest;
import com.hp.wxpay.response.WxpayResponse;

/**
 * @author HP 2022/11/22
 */
public interface WxpayClient {

    <T extends WxpayResponse> T execute(WxpayRequest<T> request) throws WxpayApiException;
}
