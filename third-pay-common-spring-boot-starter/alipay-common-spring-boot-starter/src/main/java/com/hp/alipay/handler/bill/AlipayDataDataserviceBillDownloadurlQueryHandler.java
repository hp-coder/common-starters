package com.hp.alipay.handler.bill;

import com.alipay.api.domain.AlipayDataDataserviceBillDownloadurlQueryModel;
import com.alipay.api.request.AlipayDataDataserviceBillDownloadurlQueryRequest;
import com.alipay.api.response.AlipayDataDataserviceBillDownloadurlQueryResponse;
import com.hp.alipay.context.AlipayContext;
import com.hp.alipay.handler.AbstractAlipayHandler;

import java.util.function.Supplier;

/**
 * @author HP 2022/11/14
 */
public class AlipayDataDataserviceBillDownloadurlQueryHandler extends AbstractAlipayHandler<AlipayDataDataserviceBillDownloadurlQueryModel, AlipayDataDataserviceBillDownloadurlQueryRequest, AlipayDataDataserviceBillDownloadurlQueryResponse> {
    public AlipayDataDataserviceBillDownloadurlQueryHandler(Supplier<AlipayContext<AlipayDataDataserviceBillDownloadurlQueryModel, AlipayDataDataserviceBillDownloadurlQueryRequest, AlipayDataDataserviceBillDownloadurlQueryResponse>> supplier) {
        super(supplier);
    }
}
