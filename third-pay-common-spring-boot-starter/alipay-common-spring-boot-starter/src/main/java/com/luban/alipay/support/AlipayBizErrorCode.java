package com.luban.alipay.support;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

/**
 * @author HP 2022/11/16
 */
@Getter
@AllArgsConstructor
public enum AlipayBizErrorCode implements BaseEnum<AlipayBizErrorCode> {
    SYSTEM_ERROR("ACQ.SYSTEM_ERROR", "系统错误:重新发起请求"),
    INVALID_PARAMETER("ACQ.INVALID_PARAMETER", "参数无效:检查请求参数，修改后重新发起请求"),
    TRADE_NOT_EXIST("ACQ.TRADE_NOT_EXIST", "查询的交易不存在:检查传入的交易号是否正确，修改后重新发起请求"),
    ;
    private String code;
    private String name;


    public static Optional<AlipayBizErrorCode> of(String code) {
        return Optional.ofNullable(BaseEnum.parseByCode(AlipayBizErrorCode.class, code));
    }
}
