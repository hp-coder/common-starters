package com.luban.alipay.handler.query;

import cn.hutool.core.map.MapUtil;
import com.alipay.api.domain.AlipayFundAccountQueryModel;
import com.alipay.api.request.AlipayFundAccountQueryRequest;
import com.alipay.api.response.AlipayFundAccountQueryResponse;
import com.luban.alipay.context.AlipayContext;
import com.luban.alipay.handler.AbstractAlipayHandler;
import com.luban.alipay.request.AlipayRequestUpdater;

import java.util.Map;
import java.util.function.Supplier;

/**
 * @author HP 2022/11/14
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
