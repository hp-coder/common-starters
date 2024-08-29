package com.hp.dingtalk.pojo.callback.eventbody.oa;

import com.hp.common.base.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

/**
 * @author hp
 */
@Getter
@AllArgsConstructor
public enum OaEventType implements BaseEnum<OaEventType, String> {
    /***/
    START("start","开始(审批实例开始或审批任务开始)"),
    FINISH("finish","审批正常结束（同意或拒绝）/ 审批任务转交finish表示审批任务转交。"),
    TERMINATE("terminate", "审批终止（发起人撤销审批单）")
    ;
    private final String code;
    private final String name;

    public static Optional<OaEventType> of(String code) {
        return Optional.ofNullable(BaseEnum.parseByCode(OaEventType.class, code));
    }
}
