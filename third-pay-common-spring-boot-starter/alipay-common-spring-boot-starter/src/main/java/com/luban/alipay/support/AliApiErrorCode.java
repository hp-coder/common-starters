package com.luban.alipay.support;

import com.hp.pay.support.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

/**
 * @author HP 2022/11/16
 */
@Getter
@AllArgsConstructor
public enum AliApiErrorCode implements BaseEnum<AliApiErrorCode> {
    _10000("10000", "接口调用成功"),
    _20000("20000", "服务不可用"),
    _20001("20001", "授权权限不足"),
    _40001("40001", "缺少必选参数"),
    _40002("40002", "非法的参数"),
    _40003("40003", "Insufficient Conditions（条件异常）"),
    _40004("40004", "业务处理失败"),
    _40005("40005", "Call Limited（调用频次超限）"),
    _40006("40006", "权限不足"),
    ;

    private String code;
    private String name;

    public static Optional<AliApiErrorCode> of(String code) {
        return Optional.ofNullable(BaseEnum.parseByCode(AliApiErrorCode.class, code));
    }
}
