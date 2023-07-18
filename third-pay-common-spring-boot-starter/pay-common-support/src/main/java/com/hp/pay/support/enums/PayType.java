package com.hp.pay.support.enums;

import com.hp.common.base.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

/**
 * @author HP 2022/11/15
 */
@Getter
@AllArgsConstructor
public enum PayType implements BaseEnum<PayType, String> {
    /***/
    WX("微信", "微信"),
    ALI("支付宝", "支付宝"),
    UNION("银联", "银联"),
    JD("京东", "京东"),
    OFFLINE("线下支付", "线下支付"),
    ;

    private final String code;
    private final String name;

    public static Optional<PayType> of(String code) {
        return Optional.ofNullable(BaseEnum.parseByCode(PayType.class, code));
    }
}
