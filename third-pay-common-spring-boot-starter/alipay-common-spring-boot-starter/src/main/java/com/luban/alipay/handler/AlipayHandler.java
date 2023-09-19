package com.luban.alipay.handler;

import com.alipay.api.AlipayRequest;
import com.alipay.api.AlipayResponse;
import com.luban.pay.support.PaymentHandler;

/**
 * @author hp
 */
public interface AlipayHandler<T extends AlipayRequest<R>, R extends AlipayResponse> extends PaymentHandler<R> {

}
