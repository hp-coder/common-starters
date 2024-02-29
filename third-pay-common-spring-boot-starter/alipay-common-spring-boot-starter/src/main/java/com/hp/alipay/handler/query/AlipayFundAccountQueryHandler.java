package com.hp.alipay.handler.query;

import cn.hutool.core.map.MapUtil;
import com.alipay.api.domain.AlipayFundAccountQueryModel;
import com.alipay.api.request.AlipayFundAccountQueryRequest;
import com.alipay.api.response.AlipayFundAccountQueryResponse;
import com.hp.alipay.context.AlipayContext;
import com.hp.alipay.handler.AbstractAlipayHandler;
import com.hp.alipay.request.AlipayRequestUpdater;

import java.util.Map;
import java.util.function.Supplier;

/**
 * @author hp
 */
public class AlipayFundAccountQueryHandler extends AbstractAlipayHandler<AlipayFundAccountQueryModel, AlipayFundAccountQueryRequest, AlipayFundAccountQueryResponse> {
    public AlipayFundAccountQueryHandler(Supplier<AlipayContext<AlipayFundAccountQueryModel, AlipayFundAccountQueryRequest, AlipayFundAccountQueryResponse>> supplier) {
        super(supplier);
    }



    @Override
    public AlipayRequestUpdater<AlipayFundAccountQueryModel, AlipayFundAccountQueryRequest, AlipayFundAccountQueryResponse> otherTextParam(Map<String, String> otherTextParam) {
        if (MapUtil.isNotEmpty(otherTextParam)) {
            final AlipayFundAccountQueryRequest request = this.alipayContext.request;
            otherTextParam.forEach(request::putOtherTextParam);
        }
        return this;
    }
}
