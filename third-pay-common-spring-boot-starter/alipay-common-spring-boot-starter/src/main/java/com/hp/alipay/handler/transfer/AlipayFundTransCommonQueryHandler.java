package com.hp.alipay.handler.transfer;

import cn.hutool.core.map.MapUtil;
import com.alipay.api.domain.AlipayFundTransCommonQueryModel;
import com.alipay.api.request.AlipayFundTransCommonQueryRequest;
import com.alipay.api.response.AlipayFundTransCommonQueryResponse;
import com.hp.alipay.context.AlipayContext;
import com.hp.alipay.handler.AbstractAlipayHandler;
import com.hp.alipay.request.AlipayRequestUpdater;

import java.util.Map;
import java.util.function.Supplier;

/**
 * 适用于 uniTransfer 查询*
 * @author hp
 */
public class AlipayFundTransCommonQueryHandler extends AbstractAlipayHandler<AlipayFundTransCommonQueryModel, AlipayFundTransCommonQueryRequest, AlipayFundTransCommonQueryResponse> {

    public AlipayFundTransCommonQueryHandler(Supplier<AlipayContext<AlipayFundTransCommonQueryModel, AlipayFundTransCommonQueryRequest, AlipayFundTransCommonQueryResponse>> supplier) {
        super(supplier);
    }


    @Override
    public AlipayRequestUpdater<AlipayFundTransCommonQueryModel, AlipayFundTransCommonQueryRequest, AlipayFundTransCommonQueryResponse> otherTextParam(Map<String, String> otherTextParam) {
        if (MapUtil.isNotEmpty(otherTextParam)) {
            final AlipayFundTransCommonQueryRequest request = this.alipayContext.request;
            otherTextParam.forEach(request::putOtherTextParam);
        }
        return this;
    }
}
