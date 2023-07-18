package com.hp.alipay.handler.settle;

import com.alipay.api.domain.AlipayTradeOrderSettleModel;
import com.alipay.api.request.AlipayTradeOrderSettleRequest;
import com.alipay.api.response.AlipayTradeOrderSettleResponse;
import com.hp.alipay.context.AlipayContext;
import com.hp.alipay.handler.AbstractAlipayHandler;

import java.util.function.Supplier;

/**
 * @author HP 2022/11/14
 */
public class AlipayTradeOrderSettleHandler extends AbstractAlipayHandler<AlipayTradeOrderSettleModel, AlipayTradeOrderSettleRequest, AlipayTradeOrderSettleResponse> {
    public AlipayTradeOrderSettleHandler(Supplier<AlipayContext<AlipayTradeOrderSettleModel, AlipayTradeOrderSettleRequest, AlipayTradeOrderSettleResponse>> supplier) {
        super(supplier);
    }
}
