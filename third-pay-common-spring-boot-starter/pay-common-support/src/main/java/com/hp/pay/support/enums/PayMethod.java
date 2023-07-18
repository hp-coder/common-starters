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
public enum PayMethod implements BaseEnum<PayMethod, String> {
    /***/
    JSAPI("JSAPI支付", "JSAPI支付"),
    SCAN("扫码支付", "扫码支付"),
    QRCODE("二维码支付", "二维码支付"),
    BARCODE("条形码支付", "条形码支付"),
    H5("H5支付", "H5支付"),
    APP("APP支付", "APP支付"),
    MINI("小程序支付", "小程序支付"),
    OFFLINE("线下支付", "线下支付");

    private final String code;
    private final String name;

    public static Optional<PayMethod> of(String code) {
        return Optional.ofNullable(BaseEnum.parseByCode(PayMethod.class, code));
    }
}
