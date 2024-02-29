package com.hp.alipay.request;

import com.alipay.api.AlipayObject;
import com.alipay.api.AlipayRequest;
import com.alipay.api.AlipayResponse;

/**
 * @author hp
 */
public interface AlipayRequestBuilder<T extends AlipayObject, E extends AlipayRequest<R>, R extends AlipayResponse> {

    AlipayRequestUpdater<T, E, R> request(T model);

}
