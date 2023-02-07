package com.hp.wxpay;

import com.hp.wxpay.parser.ObjectJsonParser;
import com.hp.wxpay.parser.ObjectXmlParser;
import com.hp.wxpay.parser.WxpayParser;
import com.hp.wxpay.request.WxpayRequest;
import com.hp.wxpay.response.WxpayResponse;
import com.hp.wxpay.support.WxpayConstant;

import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * @author HP 2022/11/22
 */
public abstract class AbstractWxpayClient implements WxpayClient {

    private String serverUrl;
    private String appId;
    private String mchId;
    private String slAppId;
    private String slMchId;
    private String partnerKey;
    private String apiKey;
    private String apiKey3;
    private String domain;
    private String certP12Path;
    private String keyPath;
    private String certPath;
    private String platformCertPath;
    private Object exParams;
    private X509Certificate cert;
    private int version;
    private String serialNumber;
    private Principal issuerDn;
    private Principal subjectDn;
    private Date notBefore;
    private Date notAfter;


    @Override
    public <T extends WxpayResponse> T execute(WxpayRequest<T> request) throws WxpayApiException {

        WxpayParser<T> parser;
        if (WxpayConstant.FORMAT_XML.equals(request.getFormat())) {
            parser = new ObjectXmlParser<>(request.getResponseClass());
        } else {
            parser = new ObjectJsonParser<>(request.getResponseClass());
        }
        return _execute(request, parser);
    }

    private <T extends WxpayResponse> T _execute(WxpayRequest<T> request, WxpayParser<T> parser) {
        // TODO unfinished, preRequest check
        return doRequest(request, parser);
    }

    private <T extends WxpayResponse> T doRequest(WxpayRequest<T> request, WxpayParser<T> parser) {
        return null;
    }
}
