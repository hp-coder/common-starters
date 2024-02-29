package com.hp.pay.support.enums;

import com.hp.common.base.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author hp
 */
@Getter
@AllArgsConstructor
public enum PayGroup implements BaseEnum<PayGroup, Integer> {
    /***/
    THIRD_PAY(1,"三方支付"),
    PLATFORM_PAY(2,"平台支付"),
    VIRTUAL_PROPERTY(3,"虚拟资产"),
    BANK(4,"银行卡"),
    COUPON(5,"优惠券")
    ;
    private final Integer code;
    private final String name;

    public static Optional<PayGroup> of(Integer code) {
        return Optional.ofNullable(BaseEnum.parseByCode(PayGroup.class, code));
    }

    public static Optional<PayGroup> ofName(String name) {
        return Arrays.stream(values())
                .filter(i -> Objects.equals(name, i.getName()))
                .findFirst();
    }
}
