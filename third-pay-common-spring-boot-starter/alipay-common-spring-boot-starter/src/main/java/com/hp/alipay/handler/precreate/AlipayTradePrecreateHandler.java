package com.hp.alipay.handler.precreate;

import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.hp.alipay.context.AlipayContext;
import com.hp.alipay.handler.AbstractAlipayHandler;

import java.util.function.Supplier;

/**
 * @author hp
 */
public class AlipayTradePrecreateHandler extends AbstractAlipayHandler<AlipayTradePrecreateModel, AlipayTradePrecreateRequest, AlipayTradePrecreateResponse> {


    public AlipayTradePrecreateHandler(Supplier<AlipayContext<AlipayTradePrecreateModel, AlipayTradePrecreateRequest, AlipayTradePrecreateResponse>> supplier) {
        super(supplier);
    }

}
