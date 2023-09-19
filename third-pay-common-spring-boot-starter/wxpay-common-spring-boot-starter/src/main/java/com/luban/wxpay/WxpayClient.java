package com.luban.wxpay;

import com.luban.wxpay.request.WxpayRequest;
import com.luban.wxpay.response.WxpayResponse;

/**
 * @author hp
 */
public interface WxpayClient {

    <T extends WxpayResponse> T execute(WxpayRequest<T> request) throws WxpayApiException;
}
