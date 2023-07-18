package com.hp.alipay.handler.transfer;

import cn.hutool.core.map.MapUtil;
import com.alipay.api.domain.AlipayFundTransUniTransferModel;
import com.alipay.api.request.AlipayFundTransUniTransferRequest;
import com.alipay.api.response.AlipayFundTransUniTransferResponse;
import com.hp.alipay.context.AlipayContext;
import com.hp.alipay.handler.AbstractAlipayHandler;
import com.hp.alipay.request.AlipayRequestUpdater;

import java.util.Map;
import java.util.function.Supplier;

/**
 * @author HP 2022/11/14
 */
public class AlipayFundTransUniTransferHandler extends AbstractAlipayHandler<AlipayFundTransUniTransferModel, AlipayFundTransUniTransferRequest, AlipayFundTransUniTransferResponse> {
    public AlipayFundTransUniTransferHandler(Supplier<AlipayContext<AlipayFundTransUniTransferModel, AlipayFundTransUniTransferRequest, AlipayFundTransUniTransferResponse>> supplier) {
        super(supplier);
    }

    @Override
    public AlipayRequestUpdater<AlipayFundTransUniTransferModel, AlipayFundTransUniTransferRequest, AlipayFundTransUniTransferResponse> otherTextParam(Map<String, String> otherTextParam) {
        if (MapUtil.isNotEmpty(otherTextParam)) {
            final AlipayFundTransUniTransferRequest request = this.alipayContext.request;
            otherTextParam.forEach(request::putOtherTextParam);
        }
        return this;
    }
}
