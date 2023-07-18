package com.hp.alipay.handler.refund;

import com.alipay.api.domain.AlipayTradeFastpayRefundQueryModel;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.hp.alipay.context.AlipayContext;
import com.hp.alipay.handler.AbstractAlipayHandler;

import java.util.function.Supplier;

/**
 * @author HP 2022/11/16
 */
public class AlipayTradeFastpayRefundQueryHandler extends AbstractAlipayHandler<AlipayTradeFastpayRefundQueryModel, AlipayTradeFastpayRefundQueryRequest, AlipayTradeFastpayRefundQueryResponse> {
    public AlipayTradeFastpayRefundQueryHandler(Supplier<AlipayContext<AlipayTradeFastpayRefundQueryModel, AlipayTradeFastpayRefundQueryRequest, AlipayTradeFastpayRefundQueryResponse>> supplier) {
        super(supplier);
    }
}
