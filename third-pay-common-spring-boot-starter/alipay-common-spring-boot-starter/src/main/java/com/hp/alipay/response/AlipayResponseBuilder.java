package com.hp.alipay.response;

import com.alipay.api.AlipayResponse;

/**
 * @author hp
 */
public interface AlipayResponseBuilder<R extends AlipayResponse> {

    void writeBodyAsStream(R r);
}
