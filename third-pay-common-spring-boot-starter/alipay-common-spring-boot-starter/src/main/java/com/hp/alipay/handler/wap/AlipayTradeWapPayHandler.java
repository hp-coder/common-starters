package com.hp.alipay.handler.wap;

import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.hp.alipay.context.AlipayContext;
import com.hp.alipay.handler.AbstractAlipayHandler;

import java.util.function.Supplier;

/**
 * @author hp
 */
public class AlipayTradeWapPayHandler extends AbstractAlipayHandler<AlipayTradeWapPayModel, AlipayTradeWapPayRequest, AlipayTradeWapPayResponse> {


    public AlipayTradeWapPayHandler(Supplier<AlipayContext<AlipayTradeWapPayModel, AlipayTradeWapPayRequest, AlipayTradeWapPayResponse>> supplier) {
        super(supplier);
    }


    @Override
    public AlipayTradeWapPayResponse doExecute() {
        try {
            return this.alipayContext.alipayClient.pageExecute(this.alipayContext.request);
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
    }
}
