package com.luban.wxpay;

import com.luban.wxpay.request.WxpayRequest;
import com.luban.wxpay.response.WxpayResponse;

/**
 * @author HP 2022/11/22
 */
public interface WxpayClient {

    <T extends WxpayResponse> T execute(WxpayRequest<T> request) throws WxpayApiException;
}
