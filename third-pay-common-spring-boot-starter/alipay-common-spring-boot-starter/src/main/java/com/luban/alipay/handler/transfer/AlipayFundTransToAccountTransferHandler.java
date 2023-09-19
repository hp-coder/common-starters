package com.luban.alipay.handler.transfer;

import com.alipay.api.domain.AlipayFundTransToaccountTransferModel;
import com.alipay.api.request.AlipayFundTransToaccountTransferRequest;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.luban.alipay.context.AlipayContext;
import com.luban.alipay.handler.AbstractAlipayHandler;

import java.util.function.Supplier;

/**
 * 单笔转账到支付宝账户
 * https://docs.open.alipay.com/309/106235/*
 * @author hp
 */
public class AlipayFundTransToAccountTransferHandler extends AbstractAlipayHandler<AlipayFundTransToaccountTransferModel, AlipayFundTransToaccountTransferRequest, AlipayFundTransToaccountTransferResponse> {
    public AlipayFundTransToAccountTransferHandler(Supplier<AlipayContext<AlipayFundTransToaccountTransferModel, AlipayFundTransToaccountTransferRequest, AlipayFundTransToaccountTransferResponse>> supplier) {
        super(supplier);
    }

}
