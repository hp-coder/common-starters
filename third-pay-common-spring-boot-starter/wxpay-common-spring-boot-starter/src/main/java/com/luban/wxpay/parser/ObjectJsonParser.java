package com.hp.wxpay.parser;

import com.hp.wxpay.WxpayApiException;
import com.hp.wxpay.WxpayDecryptor;
import com.hp.wxpay.request.WxpayRequest;
import com.hp.wxpay.response.WxpayResponse;

/**
 * @author HP 2022/11/24
 */
public class ObjectJsonParser<T extends WxpayResponse> implements WxpayParser<T> {

    private final Class<T> clazz;

    public ObjectJsonParser(Class<T> responseClass) {
        this.clazz = responseClass;
    }

    @Override
    public T parse(String rsp) throws WxpayApiException {
        return null;
    }

    @Override
    public Class<T> getResponseClass() throws WxpayApiException {
        return this.clazz;
    }

    @Override
    public String decryptSourceData(WxpayRequest<?> request, String body, String format, WxpayDecryptor decryptor, String encryptType, String charset) throws WxpayApiException {
        return null;
    }
}
