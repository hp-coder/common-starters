package com.hp.alipay.factory;

import com.alipay.api.*;
import com.hp.alipay.context.AlipayContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author hp
 */
public final class AlipayContextFactory {

    private AlipayContextFactory() {
    }

    public static <T extends AlipayObject, E extends AlipayRequest<R>, R extends AlipayResponse> AlipayContext<T, E, R> defaultContext() {
        final AlipayClient alipayClient;
        try {
            alipayClient = AlipayClientFactory.defaultClient();
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
        return new AlipayContext<T, E, R>(alipayClient){};
    }


    public static <T extends AlipayObject, E extends AlipayRequest<R>, R extends AlipayResponse> AlipayContext<T, E, R>
    defaultContext(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        final AlipayClient alipayClient;
        try {
            alipayClient = AlipayClientFactory.defaultClient();
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
        return new AlipayContext<T, E, R>(alipayClient, httpServletRequest, httpServletResponse){};
    }
}
