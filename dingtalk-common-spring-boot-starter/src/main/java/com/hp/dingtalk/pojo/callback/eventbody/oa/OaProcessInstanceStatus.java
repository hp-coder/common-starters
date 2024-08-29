package com.hp.dingtalk.pojo.callback.eventbody.oa;

import com.hp.common.base.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * OA审批实例状态
 *
 * @author hp
 */
@Getter
@AllArgsConstructor
public enum OaProcessInstanceStatus implements BaseEnum<OaProcessInstanceStatus, String> {
    /***/
    NEW("NEW", "创建"),
    RUNNING("RUNNING", "运行中"),
    TERMINATED("TERMINATED", "被终止"),
    COMPLETED("COMPLETED", "完成"),
    CANCELED("CANCELED", "取消"),
    ;
    private final String code;
    private final String name;

    public static Optional<OaProcessInstanceStatus> of(String code) {
        return Optional.ofNullable(BaseEnum.parseByCode(OaProcessInstanceStatus.class, code));
    }

    public static Optional<OaProcessInstanceStatus> ofName(String name) {
        return Arrays.stream(values())
                .filter(i -> Objects.equals(name, i.getName()))
                .findFirst();
    }
}
