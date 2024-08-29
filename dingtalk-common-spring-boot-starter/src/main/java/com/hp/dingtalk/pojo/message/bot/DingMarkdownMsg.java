package com.hp.dingtalk.pojo.message.bot;

import cn.hutool.core.util.StrUtil;
import com.google.common.base.Preconditions;
import com.hp.dingtalk.utils.DingMarkdown;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;


/**
 * @author hp
 */
public interface DingMarkdownMsg extends IDingBotMsg {

    @Data
    @NoArgsConstructor
    class SampleMarkdown implements DingMarkdownMsg {
        String title;
        String text;

        public SampleMarkdown(String title, DingMarkdown.Builder markdownBuilder) {
            Preconditions.checkArgument(StrUtil.isNotEmpty(title));
            Preconditions.checkArgument(Objects.nonNull(markdownBuilder));
            this.title = title;
            this.text = markdownBuilder.build();
        }

        public SampleMarkdown(String title, String markdown) {
            Preconditions.checkArgument(StrUtil.isNotEmpty(title));
            Preconditions.checkArgument(StrUtil.isNotEmpty(markdown));
            this.title = title;
            this.text = markdown;
        }
    }
}
