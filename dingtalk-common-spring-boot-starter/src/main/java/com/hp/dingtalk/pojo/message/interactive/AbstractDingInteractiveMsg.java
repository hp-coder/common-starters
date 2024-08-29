package com.hp.dingtalk.pojo.message.interactive;

import cn.hutool.core.util.StrUtil;
import com.google.common.base.Preconditions;
import com.hp.dingtalk.pojo.message.interactive.callback.IDingInteractiveCardCallBack;
import com.hp.common.base.annotation.FieldDesc;
import lombok.Data;

import java.util.Objects;

/**
 * @author hp
 */
@Data
public abstract class AbstractDingInteractiveMsg implements IDingInteractiveMsg {

    @FieldDesc("模版Id")
    public String templateId;

    @FieldDesc("回调路由key")
    public String callbackRouteKey;

    @FieldDesc("模版实例Id")
    public String outTrackId;

    @FieldDesc("简单加密")
    public String sign;

    public AbstractDingInteractiveMsg(IDingInteractiveCardCallBack callBack, String templateId, String outTrackId) {
        Preconditions.checkArgument(Objects.nonNull(callBack), "回调路由不能为空");
        Preconditions.checkArgument(StrUtil.isNotEmpty(templateId), "模版Id不能为空");
        Preconditions.checkArgument(StrUtil.isNotEmpty(outTrackId), "模版实例Id不能为空");
        this.callbackRouteKey = callBack.getCallbackRouteKey();
        this.outTrackId = outTrackId;
        this.templateId = templateId;
        this.sign = IDingInteractiveMsg.getEncryptedSign(this.outTrackId);
    }
}
