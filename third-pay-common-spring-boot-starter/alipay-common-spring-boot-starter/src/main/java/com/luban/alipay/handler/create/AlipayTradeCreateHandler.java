package com.luban.alipay.handler.create;

import com.alipay.api.domain.AlipayTradeCreateModel;
import com.alipay.api.request.AlipayTradeCreateRequest;
import com.alipay.api.response.AlipayTradeCreateResponse;
import com.luban.alipay.context.AlipayContext;
import com.luban.alipay.handler.AbstractAlipayHandler;

import java.util.function.Supplier;

/**
 * @author HP 2022/11/14
 */
public class AlipayTradeCreateHandler extends AbstractAlipayHandler<AlipayTradeCreateModel, AlipayTradeCreateRequest, AlipayTradeCreateResponse> {
    public AlipayTradeCreateHandler(Supplier<AlipayContext<AlipayTradeCreateModel, AlipayTradeCreateRequest, AlipayTradeCreateResponse>> supplier) {
        super(supplier);
    }


}
