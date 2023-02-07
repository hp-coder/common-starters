package com.hp.wxpay.parser;

import cn.hutool.core.util.XmlUtil;
import com.hp.wxpay.WxpayApiException;
import com.hp.wxpay.WxpayDecryptor;
import com.hp.wxpay.request.WxpayRequest;
import com.hp.wxpay.response.WxpayResponse;

/**
 * @author HP 2022/11/23
 */
public class ObjectXmlParser<T extends WxpayResponse> implements WxpayParser<T> {
    final Class<T> clazz;

    public ObjectXmlParser(Class<T> responseClass) {
        this.clazz = responseClass;
    }

    @Override
    public T parse(String rsp) throws WxpayApiException {
        return XmlUtil.xmlToBean(XmlUtil.parseXml(rsp).getParentNode(), clazz);
    }

    @Override
    public Class<T> getResponseClass() throws WxpayApiException {
        return this.clazz;
    }
//
//    @Override
//    public SignItem getSignItem(WxpayRequest<?> request, String responseBody) throws WxpayApiException {
//        return null;
//    }
//
//    @Override
//    public CertItem getCertItem(WxpayRequest<?> request, String responseBody) throws WxpayApiException {
//        return null;
//    }

    @Override
    public String decryptSourceData(WxpayRequest<?> request, String body, String format, WxpayDecryptor decryptor, String encryptType, String charset) throws WxpayApiException {
        return null;
    }
}
