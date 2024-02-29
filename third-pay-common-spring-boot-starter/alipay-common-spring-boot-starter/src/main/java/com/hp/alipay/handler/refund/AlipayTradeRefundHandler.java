package com.hp.alipay.handler.refund;

import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.hp.alipay.context.AlipayContext;
import com.hp.alipay.handler.AbstractAlipayHandler;

import java.util.function.Supplier;

/**
 * @author hp
 */
public class AlipayTradeRefundHandler extends AbstractAlipayHandler<AlipayTradeRefundModel, AlipayTradeRefundRequest, AlipayTradeRefundResponse> {
    public AlipayTradeRefundHandler(Supplier<AlipayContext<AlipayTradeRefundModel, AlipayTradeRefundRequest, AlipayTradeRefundResponse>> supplier) {
        super(supplier);
    }
}
