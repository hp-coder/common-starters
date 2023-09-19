package com.luban.wxpay.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * @author hp
 */
@Getter
@Setter
public class PayAmount {
    private int total;
    private String currency;

    private PayAmount() {
    }

    public PayAmount(int total) {
        this.total = total;
    }
}
