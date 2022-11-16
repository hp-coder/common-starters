package com.hp.pay.support.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author HP 2022/11/15
 */
@Getter
@AllArgsConstructor
public enum PayMethod {

    JSAPI("JSAPI支付"),
    SCAN("扫码支付"),
    QRCODE("二维码支付"),
    BARCODE("条形码支付"),
    H5("H5支付"),
    APP("APP支付"),
    MINI("小程序支付"),
    OFFLINE("线下支付");

    private String name;
}
