package com.luban.pay.support;

/**
 * @author hp
 */
public interface PaymentHandler<R> {

    default R doExecute() {
        return null;
    }
}
