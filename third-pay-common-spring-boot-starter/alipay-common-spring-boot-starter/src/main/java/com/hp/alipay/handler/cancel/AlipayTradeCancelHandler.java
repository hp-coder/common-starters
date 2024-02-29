package com.hp.alipay.handler.cancel;

import com.alipay.api.domain.AlipayTradeCancelModel;
import com.alipay.api.request.AlipayTradeCancelRequest;
import com.alipay.api.response.AlipayTradeCancelResponse;
import com.hp.alipay.context.AlipayContext;
import com.hp.alipay.handler.AbstractAlipayHandler;

import java.util.function.Supplier;

/**
 * @author hp
 */
public class AlipayTradeCancelHandler extends AbstractAlipayHandler<AlipayTradeCancelModel, AlipayTradeCancelRequest, AlipayTradeCancelResponse> {

    public AlipayTradeCancelHandler(Supplier<AlipayContext<AlipayTradeCancelModel, AlipayTradeCancelRequest, AlipayTradeCancelResponse>> supplier) {
        super(supplier);
    }
}
