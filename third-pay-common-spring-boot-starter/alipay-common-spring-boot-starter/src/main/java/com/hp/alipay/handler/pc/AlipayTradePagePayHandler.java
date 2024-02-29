package com.hp.alipay.handler.pc;

import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.hp.alipay.context.AlipayContext;
import com.hp.alipay.handler.AbstractAlipayHandler;

import java.util.function.Supplier;

/**
 * @author hp
 */
public class AlipayTradePagePayHandler extends AbstractAlipayHandler<AlipayTradePagePayModel, AlipayTradePagePayRequest, AlipayTradePagePayResponse> {

    public AlipayTradePagePayHandler(Supplier<AlipayContext<AlipayTradePagePayModel, AlipayTradePagePayRequest, AlipayTradePagePayResponse>> supplier) {
        super(supplier);
    }

    @Override
    public AlipayTradePagePayResponse doExecute() {
        try {
           return this.alipayContext.response = this.alipayContext.alipayClient.pageExecute(this.alipayContext.request);
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
    }
}
