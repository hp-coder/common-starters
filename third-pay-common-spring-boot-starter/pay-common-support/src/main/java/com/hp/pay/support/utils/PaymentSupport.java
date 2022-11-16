package com.hp.pay.support.utils;

import cn.hutool.core.util.RandomUtil;

/**
 * @author HP 2022/11/15
 */
public final class PaymentSupport {

    private PaymentSupport() {
    }

    public static String outTrackNumber() {
        return System.currentTimeMillis() + RandomUtil.randomNumbers(10);
    }
}
