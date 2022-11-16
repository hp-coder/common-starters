package com.hp.alipay.handler.close;

import com.alipay.api.domain.AlipayTradeCloseModel;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.hp.alipay.context.AlipayContext;
import com.hp.alipay.handler.AbstractAlipayHandler;

import java.util.function.Supplier;

/**
 * @author HP 2022/11/14
 */
public class AlipayTradeCloseHandler extends AbstractAlipayHandler<AlipayTradeCloseModel, AlipayTradeCloseRequest, AlipayTradeCloseResponse> {
    public AlipayTradeCloseHandler(Supplier<AlipayContext<AlipayTradeCloseModel, AlipayTradeCloseRequest, AlipayTradeCloseResponse>> supplier) {
        super(supplier);
    }
}
