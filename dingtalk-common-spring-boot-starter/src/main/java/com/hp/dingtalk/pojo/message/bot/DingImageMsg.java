package com.hp.dingtalk.pojo.message.bot;

import cn.hutool.core.util.StrUtil;
import com.google.common.base.Preconditions;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hp
 */
public interface DingImageMsg extends IDingBotMsg {

    @Data
    @NoArgsConstructor
    class SampleImageMsg implements DingImageMsg {
        @SerializedName("photoURL")
        String photoUrl;

        public SampleImageMsg(String photoUrl) {
            Preconditions.checkArgument(StrUtil.isNotEmpty(photoUrl));
            this.photoUrl = photoUrl;
        }
    }
}
