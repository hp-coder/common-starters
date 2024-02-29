package com.hp.pay.support.enums;

import com.hp.common.base.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

/**
 * @author hp
 */
@Getter
@AllArgsConstructor
public enum PayMethod implements BaseEnum<PayMethod, Integer> {
    /***/
    JSAPI(1, "JSAPI支付"),
    SCAN(2, "扫码支付"),
    QRCODE(3, "二维码支付"),
    BARCODE(4, "条形码支付"),
    H5(5, "H5支付"),
    APP(6, "APP支付"),
    MINI(7, "小程序支付"),
    OFFLINE(8, "线下支付"),
    COIN(9, "虚拟币"),
    COUPON(10, "优惠券"),
    ;

    private final Integer code;
    private final String name;

    public static Optional<PayMethod> of(Integer code) {
        return Optional.ofNullable(BaseEnum.parseByCode(PayMethod.class, code));
    }
}
