package com.luban.alipay.handler.auth;

import com.alipay.api.domain.AlipayOpenAuthTokenAppModel;
import com.alipay.api.request.AlipayOpenAuthTokenAppRequest;
import com.alipay.api.response.AlipayOpenAuthTokenAppResponse;
import com.luban.alipay.context.AlipayContext;
import com.luban.alipay.handler.AbstractAlipayHandler;

import java.util.function.Supplier;

/**
 * @author HP 2022/11/14
 */
public class AlipayOpenAuthTokenAppHandler extends AbstractAlipayHandler<AlipayOpenAuthTokenAppModel, AlipayOpenAuthTokenAppRequest, AlipayOpenAuthTokenAppResponse> {
    public AlipayOpenAuthTokenAppHandler(Supplier<AlipayContext<AlipayOpenAuthTokenAppModel, AlipayOpenAuthTokenAppRequest, AlipayOpenAuthTokenAppResponse>> supplier) {
        super(supplier);
    }
}
