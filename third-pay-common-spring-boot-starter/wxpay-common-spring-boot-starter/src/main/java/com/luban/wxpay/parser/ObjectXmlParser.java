package com.luban.wxpay.parser;

import cn.hutool.core.util.XmlUtil;
import com.luban.wxpay.WxpayApiException;
import com.luban.wxpay.WxpayDecryptor;
import com.luban.wxpay.request.WxpayRequest;
import com.luban.wxpay.response.WxpayResponse;

/**
 * @author hp
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
