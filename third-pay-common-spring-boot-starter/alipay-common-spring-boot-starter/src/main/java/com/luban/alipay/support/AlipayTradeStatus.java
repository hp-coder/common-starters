package com.luban.alipay.support;

import com.luban.common.base.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

/**
 * @author HP 2022/11/16
 */
@Getter
@AllArgsConstructor
public enum AlipayTradeStatus implements BaseEnum<AlipayTradeStatus,String> {

    WAIT_BUYER_PAY("WAIT_BUYER_PAY", "交易创建，等待付款"),
    TRADE_CLOSED("TRADE_CLOSED", "交易关闭"),
    TRADE_SUCCESS("TRADE_SUCCESS", "交易支付成功"),
    TRADE_FINISHED("TRADE_FINISHED", "交易结束，不可退款"),
    ;
    private String code;
    private String name;

    public static Optional<AlipayTradeStatus> of(String code) {
        return Optional.ofNullable(BaseEnum.parseByCode(AlipayTradeStatus.class, code));
    }
}
