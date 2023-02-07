package com.luban.alipay.handler.app;

import com.alipay.api.AlipayApiException;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.luban.alipay.context.AlipayContext;
import com.luban.alipay.handler.AbstractAlipayHandler;

import java.util.function.Supplier;

/**
 * @author HP 2022/11/14
 */
public class AlipayTradeAppPayHandler extends AbstractAlipayHandler<AlipayTradeAppPayModel, AlipayTradeAppPayRequest, AlipayTradeAppPayResponse> {

    public AlipayTradeAppPayHandler(Supplier<AlipayContext<AlipayTradeAppPayModel, AlipayTradeAppPayRequest, AlipayTradeAppPayResponse>> supplier) {
        super(supplier);
    }

    @Override
    public AlipayTradeAppPayResponse doExecute() {
        try {
            return this.alipayContext.alipayClient.sdkExecute(this.alipayContext.request);
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
    }
}
