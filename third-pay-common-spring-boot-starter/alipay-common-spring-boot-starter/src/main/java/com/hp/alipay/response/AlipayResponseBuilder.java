package com.hp.alipay.response;

import com.alipay.api.AlipayResponse;

/**
 * @author HP 2022/11/11
 */
public interface AlipayResponseBuilder<R extends AlipayResponse> {

    void writeBodyAsStream(R r);
}
