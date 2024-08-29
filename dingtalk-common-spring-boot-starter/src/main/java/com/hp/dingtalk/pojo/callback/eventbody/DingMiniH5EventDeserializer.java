package com.hp.dingtalk.pojo.callback.eventbody;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.hp.dingtalk.constant.minih5event.DingMiniH5Event;

import java.lang.reflect.Type;

/**
 * @author hp
 */
public class DingMiniH5EventDeserializer implements JsonDeserializer<DingMiniH5Event> {
    @Override
    public DingMiniH5Event deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return DingMiniH5Event.of(json.getAsString()).orElse(null);
    }
}
