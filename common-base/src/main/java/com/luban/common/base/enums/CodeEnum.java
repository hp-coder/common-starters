package com.luban.common.base.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

@Getter
@AllArgsConstructor
public enum CodeEnum implements com.luban.common.base.enums.BaseEnum<CodeEnum> {
    Success(1, "操作成功"),
    Fail(0, "操作失败"),
    NotFindError(10001, "未查询到信息"),
    SaveError(10002, "保存信息失败"),
    UpdateError(10003, "更新信息失败"),
    ValidateError(10004, "数据检验失败"),
    StatusHasValid(10005, "状态已经被启用"),
    StatusHasInvalid(10006, "状态已经被禁用"),
    SystemError(10007, "系统异常"),
    BusinessError(10008, "业务异常"),
    ParamSetIllegal(10009, "参数设置非法"),
    TransferStatusError(10010, "当前状态不正确，请勿重复提交"),
    NotGrant(10011, "没有操作该功能的权限，请联系管理员");

    private Integer code;
    private String name;

    public static Optional<CodeEnum> of(Integer code) {
        return Optional.ofNullable(BaseEnum.parseByCode(CodeEnum.class, code));
    }
}
