package com.hp.alipay.request;

import com.alipay.api.AlipayObject;
import com.alipay.api.AlipayRequest;
import com.alipay.api.AlipayResponse;

/**
 * @author HP 2022/11/11
 */
public interface AlipayRequestBuilder<T extends AlipayObject, E extends AlipayRequest<R>, R extends AlipayResponse> {

    AlipayRequestUpdater<T, E, R> request(T model);

}
