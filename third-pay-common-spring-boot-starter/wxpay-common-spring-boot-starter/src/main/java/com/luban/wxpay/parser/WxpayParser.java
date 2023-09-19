package com.luban.wxpay.parser;

import com.luban.wxpay.WxpayApiException;
import com.luban.wxpay.WxpayDecryptor;
import com.luban.wxpay.request.WxpayRequest;
import com.luban.wxpay.response.WxpayResponse;

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
