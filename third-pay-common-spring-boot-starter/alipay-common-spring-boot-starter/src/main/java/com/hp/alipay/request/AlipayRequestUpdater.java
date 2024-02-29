package com.hp.alipay.request;

import com.alipay.api.AlipayObject;
import com.alipay.api.AlipayRequest;
import com.alipay.api.AlipayResponse;
import com.hp.alipay.handler.AlipayHandler;

import java.util.Map;

/**
 * @author hp
 */
public interface AlipayRequestUpdater<T extends AlipayObject, E extends AlipayRequest<R>, R extends AlipayResponse> {


    AlipayRequestUpdater<T, E, R> notifyUrl(String notifyUrl);

    AlipayRequestUpdater<T, E, R> returnUrl(String returnUrl);

    default AlipayRequestUpdater<T, E, R> otherTextParam(Map<String, String> otherTextParam) {
        return this;
    }

    AlipayRequestUpdater<T, E, R> certificatedRequest(boolean isCertificatedRequest);

    AlipayRequestUpdater<T, E, R> accessToken(String accessToken);

    AlipayRequestUpdater<T, E, R> appAuthToken(String appAuthToken);

    AlipayRequestUpdater<T, E, R> targetAppId(String targetAppId);

    AlipayHandler<E, R> handler();
}
