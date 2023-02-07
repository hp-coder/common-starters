package com.luban.alipay.handler.precreate;

import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.luban.alipay.context.AlipayContext;
import com.luban.alipay.handler.AbstractAlipayHandler;

import java.util.function.Supplier;

/**
 * @author HP 2022/11/11
 */
public class AlipayTradePrecreateHandler extends AbstractAlipayHandler<AlipayTradePrecreateModel, AlipayTradePrecreateRequest, AlipayTradePrecreateResponse> {


    public AlipayTradePrecreateHandler(Supplier<AlipayContext<AlipayTradePrecreateModel, AlipayTradePrecreateRequest, AlipayTradePrecreateResponse>> supplier) {
        super(supplier);
    }

}
