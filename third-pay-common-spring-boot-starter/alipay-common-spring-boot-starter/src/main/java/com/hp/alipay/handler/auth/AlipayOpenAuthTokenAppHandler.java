package com.hp.alipay.handler.auth;

import com.alipay.api.domain.AlipayOpenAuthTokenAppModel;
import com.alipay.api.request.AlipayOpenAuthTokenAppRequest;
import com.alipay.api.response.AlipayOpenAuthTokenAppResponse;
import com.hp.alipay.context.AlipayContext;
import com.hp.alipay.handler.AbstractAlipayHandler;

import java.util.function.Supplier;

/**
 * @author hp
 */
public class AlipayOpenAuthTokenAppHandler extends AbstractAlipayHandler<AlipayOpenAuthTokenAppModel, AlipayOpenAuthTokenAppRequest, AlipayOpenAuthTokenAppResponse> {
    public AlipayOpenAuthTokenAppHandler(Supplier<AlipayContext<AlipayOpenAuthTokenAppModel, AlipayOpenAuthTokenAppRequest, AlipayOpenAuthTokenAppResponse>> supplier) {
        super(supplier);
    }
}
