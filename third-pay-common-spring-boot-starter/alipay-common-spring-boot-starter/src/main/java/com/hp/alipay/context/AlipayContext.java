package com.hp.alipay.context;

import cn.hutool.extra.spring.SpringUtil;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayObject;
import com.alipay.api.AlipayRequest;
import com.alipay.api.AlipayResponse;
import com.hp.alipay.config.AlipayClientConfig;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author hp
 */
public abstract class AlipayContext<T extends AlipayObject, E extends AlipayRequest<? extends AlipayResponse>, R extends AlipayResponse> {
    public final AlipayClient alipayClient;
    public T model;
    public E request;
    public R response;

    public HttpServletRequest httpServletRequest;
    public HttpServletResponse httpServletResponse;

    public String appAuthToken;
    public String targetAppId;
    public String accessToken;
    public Boolean certificatedRequest;
    public String notifyUrl;

    public AlipayContext(AlipayClient alipayClient) {
        this.alipayClient = alipayClient;
        init();
    }

    public AlipayContext(AlipayClient alipayClient, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        this.alipayClient = alipayClient;
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
        init();
    }

    private void init() {
        final AlipayClientConfig config = SpringUtil.getBean(AlipayClientConfig.class);
        this.certificatedRequest = config.isGlobalCertificatedRequest();
        this.notifyUrl = config.getNotifyUrl();
    }
}
