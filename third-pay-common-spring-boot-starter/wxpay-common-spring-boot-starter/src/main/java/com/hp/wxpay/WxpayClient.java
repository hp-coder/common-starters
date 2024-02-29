package com.hp.wxpay;

import com.hp.wxpay.request.WxpayRequest;
import com.hp.wxpay.response.WxpayResponse;

/**
 * @author hp
 */
public interface WxpayClient {

    <T extends WxpayResponse> T execute(WxpayRequest<T> request) throws WxpayApiException;
}
