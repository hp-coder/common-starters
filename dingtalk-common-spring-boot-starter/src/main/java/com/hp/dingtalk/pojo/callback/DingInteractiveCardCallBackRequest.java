package com.hp.dingtalk.pojo.callback;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hp.common.base.model.Request;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 用于封装钉钉互动卡片的回调请求体对象
 *
 * @author hp
 */
@Getter
@Setter
public class DingInteractiveCardCallBackRequest implements Request {

    private String outTrackId;
    private String corpId;
    private String userId;
    private String value;
    private String content;

    public Value getValue() {
        return new Gson().fromJson(value, new TypeToken<Value>() {
        }.getType());
    }

    public Content getContent() {
        return new Gson().fromJson(content, new TypeToken<Content>() {
        }.getType());
    }

    @Getter
    @Setter
    public static class Content {
        private CardPrivateData cardPrivateData;
    }

    @Getter
    @Setter
    public static class Value {
        private CardPrivateData cardPrivateData;
    }


    @Setter
    public static class CardPrivateData {
        @Getter
        private List<String> actionIds;
        private Object params;

        public <T> T getParams(Class<T> tClass) {
            final Gson gson = new Gson();
            return gson.fromJson(gson.toJson(params), tClass);
        }
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DingInteractiveCardCallBackRequest{");
        sb.append("outTrackId='").append(outTrackId).append('\'');
        sb.append(", getCorpId='").append(corpId).append('\'');
        sb.append(", userId='").append(userId).append('\'');
        sb.append(", value='").append(value).append('\'');
        sb.append(", content='").append(content).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
