package com.hp.pay.support;

/**
 * @author HP 2022/11/11
 */
public interface PaymentHandler<R> {

    default R doExecute() {
        return null;
    }
}
