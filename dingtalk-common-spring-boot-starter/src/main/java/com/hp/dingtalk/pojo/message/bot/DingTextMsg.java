
package com.hp.dingtalk.pojo.message.bot;


import cn.hutool.core.util.StrUtil;
import com.google.common.base.Preconditions;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author hp
 */
public interface DingTextMsg extends IDingBotMsg {

    @Data
    @NoArgsConstructor
    class SampleText implements DingTextMsg {
        String content;

        public SampleText(String content) {
            Preconditions.checkArgument(StrUtil.isNotEmpty(content));
            this.content = content;
        }
    }
}
