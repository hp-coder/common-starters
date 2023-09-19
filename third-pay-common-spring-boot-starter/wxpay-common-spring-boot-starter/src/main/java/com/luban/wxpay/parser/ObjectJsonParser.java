package com.luban.wxpay.parser;

import com.luban.wxpay.WxpayApiException;
import com.luban.wxpay.WxpayDecryptor;
import com.luban.wxpay.request.WxpayRequest;
import com.luban.wxpay.response.WxpayResponse;

/**
 * @author hp
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
