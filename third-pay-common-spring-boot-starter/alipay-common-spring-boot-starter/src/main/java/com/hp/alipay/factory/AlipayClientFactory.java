package com.hp.alipay.factory;

import cn.hutool.extra.spring.SpringUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.hp.alipay.config.AlipayClientConfig;

/**
 * @author HP 2022/11/11
 */
public final class AlipayClientFactory {

    private  AlipayClientFactory(){}

    public static AlipayClient defaultClient() throws AlipayApiException {
        return new DefaultAlipayClient(SpringUtil.getBean(AlipayClientConfig.class));
    }
}
