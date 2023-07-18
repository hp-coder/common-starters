package com.hp.alipay.handler.transfer;

import com.alipay.api.domain.AlipayFundTransOrderQueryModel;
import com.alipay.api.request.AlipayFundTransOrderQueryRequest;
import com.alipay.api.response.AlipayFundTransOrderQueryResponse;
import com.hp.alipay.context.AlipayContext;
import com.hp.alipay.handler.AbstractAlipayHandler;

import java.util.function.Supplier;

/**
 * 查转账*
 * @author HP 2022/11/14
 */
public class AlipayFundTransOrderQueryHandler extends AbstractAlipayHandler<AlipayFundTransOrderQueryModel, AlipayFundTransOrderQueryRequest, AlipayFundTransOrderQueryResponse> {
    public AlipayFundTransOrderQueryHandler(Supplier<AlipayContext<AlipayFundTransOrderQueryModel, AlipayFundTransOrderQueryRequest, AlipayFundTransOrderQueryResponse>> supplier) {
        super(supplier);
    }

}
