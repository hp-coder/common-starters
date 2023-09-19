package com.luban.pay.support.enums;

import com.luban.common.base.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

/**
 * @author hp
 */
@Getter
@AllArgsConstructor
public enum PayType implements BaseEnum<PayType, Integer> {
    /***/
    WECHAT_PAY(1, "微信"),
    ALIPAY(2, "支付宝"),
    UNION_PAY(3, "银联"),
    JD_PAY(4, "京东"),
    OFFLINE(5, "线下支付"),
    COIN(6, "线下支付"),
    COUPON(7, "线下支付"),
    ;

    private final Integer code;
    private final String name;

    public static Optional<PayType> of(Integer code) {
        return Optional.ofNullable(BaseEnum.parseByCode(PayType.class, code));
    }
}
