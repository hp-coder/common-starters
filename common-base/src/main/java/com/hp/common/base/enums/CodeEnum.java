package com.hp.common.base.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

/**
 * @author hp
 */
@Getter
@AllArgsConstructor
public enum CodeEnum implements BaseEnum<CodeEnum,Integer> {
    /***/
    Success(200, "操作成功"),
    Fail(500, "操作失败"),
    NotFindError(500, "未查询到信息"),
    SaveError(500, "保存信息失败"),
    UpdateError(500, "更新信息失败"),
    ValidateError(500, "数据检验失败"),
    StatusHasValid(500, "状态已经被启用"),
    StatusHasInvalid(500, "状态已经被禁用"),
    SystemError(500, "系统异常"),
    BusinessError(500, "业务异常"),
    ParamSetIllegal(500, "参数设置非法"),
    TransferStatusError(500, "当前状态不正确，请勿重复提交"),
    NotGrant(500, "没有操作该功能的权限，请联系管理员");

    private final Integer code;
    private final String name;

    public static Optional<CodeEnum> of(Integer code) {
        return Optional.ofNullable(BaseEnum.parseByCode(CodeEnum.class, code));
    }
}
