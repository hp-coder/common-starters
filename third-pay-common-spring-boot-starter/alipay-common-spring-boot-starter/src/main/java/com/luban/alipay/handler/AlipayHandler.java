package com.luban.alipay.handler;

import com.alipay.api.AlipayRequest;
import com.alipay.api.AlipayResponse;
import com.hp.pay.support.PaymentHandler;

/**
 * @author HP 2022/11/11
 */
public interface AlipayHandler<T extends AlipayRequest<R>, R extends AlipayResponse> extends PaymentHandler<R> {

}
