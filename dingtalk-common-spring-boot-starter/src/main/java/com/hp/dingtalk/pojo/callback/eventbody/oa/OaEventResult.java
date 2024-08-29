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
public enum OaEventResult implements BaseEnum<OaEventResult, String> {
    /**
     * 审批终止时没这个值
     */
    AGREE("agree", "正常结束时result为agree"),
    REFUSE("refuse", "拒绝时result为refuse"),
    REDIRECT("redirect","表示审批任务转交。")
    ;
    private final String code;
    private final String name;

    public static Optional<OaEventResult> of(String code) {
        return Optional.ofNullable(BaseEnum.parseByCode(OaEventResult.class, code));
    }
}
