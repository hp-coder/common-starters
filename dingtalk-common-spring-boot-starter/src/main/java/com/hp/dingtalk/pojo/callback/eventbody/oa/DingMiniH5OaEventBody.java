package com.hp.dingtalk.pojo.callback.eventbody.oa;

import com.google.gson.annotations.SerializedName;
import com.hp.dingtalk.constant.minih5event.DingMiniH5Event;
import com.hp.common.base.annotation.FieldDesc;
import com.hp.dingtalk.pojo.callback.eventbody.IDingMiniH5EventBody;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * OA审批事件payload
 *
 * @author hp
 */
@NoArgsConstructor
@Data
public class DingMiniH5OaEventBody implements IDingMiniH5EventBody {

    @FieldDesc("事件类型")
    @SerializedName(value = "eventType", alternate = "EventType")
    private DingMiniH5Event eventType;

    @FieldDesc("审批实例Id")
    private String processInstanceId;

    @FieldDesc("审批实例对应的企业corpId")
    private String corpId;

    @FieldDesc("创建审批实例时间。时间戳，单位毫秒。")
    private Long createTime;

    @FieldDesc("审批结束时间,时间戳,ms")
    private Long finishTime;

    @FieldDesc("实例标题")
    private String title;

    @FieldDesc("类型")
    private OaEventType type;

    @FieldDesc("发起审批实例的员工userId。")
    private String staffId;

    @FieldDesc("审批实例url，可在钉钉内跳转到审批页面")
    private String url;

    @FieldDesc("审批模板的唯一码。")
    private String processCode;

    @FieldDesc("正常结束时result为agree，拒绝时result为refuse，审批终止时没这个值。")
    private OaEventResult result;

    @FieldDesc("审批任务结束时, remark表示操作时写的评论内容。")
    private String remark;

    public boolean approved(){
        final boolean agreed = Objects.equals(OaEventResult.AGREE,  getResult());
        final boolean finished = Objects.equals(OaEventType.FINISH,  getType());
        return agreed && finished;
    }

    public boolean disapproved(){
        return !approved();
    }
}
