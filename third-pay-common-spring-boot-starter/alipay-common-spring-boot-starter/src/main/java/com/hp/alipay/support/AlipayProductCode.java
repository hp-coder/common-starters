package com.hp.alipay.support;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author HP 2022/11/15
 */
@Getter
@AllArgsConstructor
public enum AlipayProductCode {

    /**
     * 周期扣款
     */
    CYCLE_PAY_AUTH("CYCLE_PAY_AUTH"),
    /**
     * 无线快捷
     */
    QUICK_MSECURITY_PAY("QUICK_MSECURITY_PAY"),

    /**
     * 当面付，当product_code不传时，使用该默认值
     */
    NATIVE("FACE_TO_FACE_PAYMENT"),
    /**
     * 手机网站支付
     */
    WAP("QUICK_WAP_PAY");

    private String code;
}
