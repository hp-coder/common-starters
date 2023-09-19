package com.luban.alipay.handler.cancel;

import com.alipay.api.domain.AlipayTradeCancelModel;
import com.alipay.api.request.AlipayTradeCancelRequest;
import com.alipay.api.response.AlipayTradeCancelResponse;
import com.luban.alipay.context.AlipayContext;
import com.luban.alipay.handler.AbstractAlipayHandler;

import java.util.function.Supplier;

/**
 * @author hp
 */
public class AlipayTradeCancelHandler extends AbstractAlipayHandler<AlipayTradeCancelModel, AlipayTradeCancelRequest, AlipayTradeCancelResponse> {

    public AlipayTradeCancelHandler(Supplier<AlipayContext<AlipayTradeCancelModel, AlipayTradeCancelRequest, AlipayTradeCancelResponse>> supplier) {
        super(supplier);
    }
}
