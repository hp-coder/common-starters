package com.hp.dingtalk.pojo.callback.eventbody.oa;

import com.google.gson.GsonBuilder;
import com.hp.dingtalk.constant.minih5event.DingMiniH5Event;
import com.hp.dingtalk.pojo.GsonBuilderVisitor;
import com.hp.dingtalk.pojo.callback.eventbody.DingMiniH5EventDeserializer;

/**
 * @author hp
 */
public class DingMiniH5OaEventBodyGsonBuilderVisitor implements GsonBuilderVisitor {
    @Override
    public void customize(GsonBuilder builder) {
        builder.registerTypeAdapter(OaEventType.class, new OaEventTypeDeserializer());
        builder.registerTypeAdapter(OaEventResult.class, new OaEventResultDeserializer());
        builder.registerTypeAdapter(DingMiniH5Event.class, new DingMiniH5EventDeserializer());
    }
}
