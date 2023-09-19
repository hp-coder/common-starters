package com.luban.alipay.handler.bill;

import com.alipay.api.domain.AlipayDataDataserviceBillDownloadurlQueryModel;
import com.alipay.api.request.AlipayDataDataserviceBillDownloadurlQueryRequest;
import com.alipay.api.response.AlipayDataDataserviceBillDownloadurlQueryResponse;
import com.luban.alipay.context.AlipayContext;
import com.luban.alipay.handler.AbstractAlipayHandler;

import java.util.function.Supplier;

/**
 * @author hp
 */
public class AlipayDataDataserviceBillDownloadurlQueryHandler extends AbstractAlipayHandler<AlipayDataDataserviceBillDownloadurlQueryModel, AlipayDataDataserviceBillDownloadurlQueryRequest, AlipayDataDataserviceBillDownloadurlQueryResponse> {
    public AlipayDataDataserviceBillDownloadurlQueryHandler(Supplier<AlipayContext<AlipayDataDataserviceBillDownloadurlQueryModel, AlipayDataDataserviceBillDownloadurlQueryRequest, AlipayDataDataserviceBillDownloadurlQueryResponse>> supplier) {
        super(supplier);
    }
}
