package com.hp.wxpay.parser;

import com.hp.wxpay.WxpayApiException;
import com.hp.wxpay.WxpayDecryptor;
import com.hp.wxpay.request.WxpayRequest;
import com.hp.wxpay.response.WxpayResponse;

/**
 * @author hp
 */
public interface WxpayParser<T extends WxpayResponse> {

    T parse(String rsp) throws WxpayApiException;


    /**
     * 获取响应类类型。
     */
    Class<T> getResponseClass() throws WxpayApiException;

//    /**
//     * 获取响应内的签名数据
//     */
//    SignItem getSignItem(WxpayRequest<?> request, String responseBody)
//            throws WxpayApiException;
//
//    /**
//     * 获取响应内的证书序列号和签名数据
//     */
//    CertItem getCertItem(WxpayRequest<?> request, String responseBody)
//            throws WxpayApiException;

    /**
     * 获取实际串：如果是加密内容则返回内容已经是解密后的实际内容了
     */
    String decryptSourceData(WxpayRequest<?> request, String body, String format,
                             WxpayDecryptor decryptor, String encryptType, String charset)
            throws WxpayApiException;

}
