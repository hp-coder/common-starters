package com.luban.alipay.handler.settle;

import com.alipay.api.domain.AlipayTradeOrderSettleModel;
import com.alipay.api.request.AlipayTradeOrderSettleRequest;
import com.alipay.api.response.AlipayTradeOrderSettleResponse;
import com.luban.alipay.context.AlipayContext;
import com.luban.alipay.handler.AbstractAlipayHandler;

import java.util.function.Supplier;

/**
 * @author hp
 */
public class AlipayTradeOrderSettleHandler extends AbstractAlipayHandler<AlipayTradeOrderSettleModel, AlipayTradeOrderSettleRequest, AlipayTradeOrderSettleResponse> {
    public AlipayTradeOrderSettleHandler(Supplier<AlipayContext<AlipayTradeOrderSettleModel, AlipayTradeOrderSettleRequest, AlipayTradeOrderSettleResponse>> supplier) {
        super(supplier);
    }
}
