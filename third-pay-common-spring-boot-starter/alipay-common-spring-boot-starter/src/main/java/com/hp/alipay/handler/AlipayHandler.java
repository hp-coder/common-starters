package com.hp.alipay.handler;

import com.alipay.api.AlipayRequest;
import com.alipay.api.AlipayResponse;
import com.hp.pay.support.PaymentHandler;

/**
 * @author hp
 */
public interface AlipayHandler<T extends AlipayRequest<R>, R extends AlipayResponse> extends PaymentHandler<R> {

}
