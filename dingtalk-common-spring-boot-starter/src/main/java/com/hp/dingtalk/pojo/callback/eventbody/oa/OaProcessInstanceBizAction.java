package com.hp.dingtalk.pojo.callback.eventbody.oa;

import com.hp.common.base.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * 审批实例业务动作
 *
 * @author hp
 */
@Getter
@AllArgsConstructor
public enum OaProcessInstanceBizAction implements BaseEnum<OaProcessInstanceBizAction, String> {
    /***/
    MODIFY("MODIFY", "表示该审批实例是基于原来的实例修改而来"),
    REVOKE("REVOKE", "表示该审批实例是由原来的实例撤销后重新发起的"),
    NONE("NONE", "表示正常发起"),
    ;
    private final String code;
    private final String name;

    public static Optional<OaProcessInstanceBizAction> of(String code) {
        return Optional.ofNullable(BaseEnum.parseByCode(OaProcessInstanceBizAction.class, code));
    }

    public static Optional<OaProcessInstanceBizAction> ofName(String name) {
        return Arrays.stream(values())
                .filter(i -> Objects.equals(name, i.getName()))
                .findFirst();
    }
}
