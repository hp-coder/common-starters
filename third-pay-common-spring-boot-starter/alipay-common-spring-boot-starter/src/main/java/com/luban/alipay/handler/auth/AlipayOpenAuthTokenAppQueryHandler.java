package com.luban.alipay.handler.auth;

import com.alipay.api.domain.AlipayOpenAuthTokenAppQueryModel;
import com.alipay.api.request.AlipayOpenAuthTokenAppQueryRequest;
import com.alipay.api.response.AlipayOpenAuthTokenAppQueryResponse;
import com.luban.alipay.context.AlipayContext;
import com.luban.alipay.handler.AbstractAlipayHandler;

import java.util.function.Supplier;

/**
 * @author HP 2022/11/14
 */
public class AlipayOpenAuthTokenAppQueryHandler extends AbstractAlipayHandler<AlipayOpenAuthTokenAppQueryModel, AlipayOpenAuthTokenAppQueryRequest, AlipayOpenAuthTokenAppQueryResponse> {

    public AlipayOpenAuthTokenAppQueryHandler(Supplier<AlipayContext<AlipayOpenAuthTokenAppQueryModel, AlipayOpenAuthTokenAppQueryRequest, AlipayOpenAuthTokenAppQueryResponse>> supplier) {
        super(supplier);
    }
}
