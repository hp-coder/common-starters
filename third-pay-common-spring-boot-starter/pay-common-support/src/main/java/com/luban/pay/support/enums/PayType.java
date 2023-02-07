package com.hp.pay.support.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author HP 2022/11/15
 */
@Getter
@AllArgsConstructor
public enum PayType {

    WX("微信"),
    ALI("支付宝"),
    UNION("银联"),
    JD("京东"),
    OFFLINE("线下支付");

    private String name;
}
