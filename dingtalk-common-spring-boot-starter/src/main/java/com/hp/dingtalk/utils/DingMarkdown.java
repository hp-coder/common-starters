package com.hp.dingtalk.utils;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hp
 */
public class DingMarkdown {

    /*
            标题
        # 一级标题
        ## 二级标题
        ### 三级标题
        #### 四级标题
        ##### 五级标题
        ###### 六级标题

        引用
        > A man who stands for nothing will fall for anything.

        文字加粗、斜体
        **bold**
        *italic*

        链接
        [this is a link](https://www.dingtalk.com/)

        图片
        ![](http://name.com/pic.jpg)

        无序列表
        - item1
        - item2

        有序列表
        1. item1
        2. item2

        换行(建议\n前后各添加两个空格)
          \n
     */

    private static final String DING_MSG_BACKGROUND_COLOR = "#FFFFFF";
    private final List<String> fullContent;

    public DingMarkdown(Builder builder) {
        fullContent = builder.fullContent;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        List<String> fullContent = new ArrayList<>();

        public Builder contentTitle(String contentTitle) {
            fullContent.add(contentTitle);
            return this;
        }

        public Builder level1Title(String level1Title) {
            fullContent.add("# " + level1Title);
            return this;
        }

        public Builder level2Title(String level2Title) {
            fullContent.add("## " + level2Title);
            return this;
        }

        public Builder level3Title(String level3Title) {
            fullContent.add("### " + level3Title);
            return this;
        }

        public Builder level4Title(String level4Title) {
            fullContent.add("#### " + level4Title);
            return this;
        }

        public Builder level5Title(String level5Title) {
            fullContent.add("##### " + level5Title);
            return this;
        }

        public Builder level6Title(String level6Title) {
            fullContent.add("###### " + level6Title);
            return this;
        }

        public Builder reference(String reference) {
            fullContent.add("> " + reference);
            return this;
        }

        public Builder text(String text) {
            fullContent.add(text);
            return this;
        }

        public Font textWithFont(String text) {
            return new Font(this, text);
        }

        public Builder boldText(String boldText) {
            fullContent.add("**" + boldText + "**");
            return this;
        }

        public Builder italicText(String italicText) {
            fullContent.add("*" + italicText + "*");
            return this;
        }

        public Builder link(String name, String url) {
            fullContent.add("[" + name + "](" + url + ")");
            return this;
        }

        public Builder image(String url) {
            fullContent.add("![](" + url + ")");
            return this;
        }

        public Builder disorderedList(String... element) {
            if (element.length > 0) {
                final String elements = Arrays.stream(element).map(i -> "- " + i).collect(Collectors.joining("  \n  "));
                fullContent.add(elements);
            }
            return this;
        }

        public Builder orderedList(String... element) {
            if (element.length > 0) {
                StringBuilder strBuilder = new StringBuilder();
                for (int i = 0; i < element.length; i++) {
                    strBuilder.append(i).append(1).append(". ").append(element[i]).append("  \n  ");
                }
                fullContent.add(strBuilder.toString());
            }
            return this;
        }

        public Builder newLine() {
            return this.textWithFont(".").color(DING_MSG_BACKGROUND_COLOR).face("monospace").builder();
        }

        public String build() {
            final DingMarkdown dingMarkdown = new DingMarkdown(this);
            return String.join("  \n  ", dingMarkdown.fullContent);
        }
    }


    public static class Font {

        private String temp = "<font face=\"{face}\" color=\"{color}\">{text}</font>";
        private final Builder builder;


        public Font(Builder builder, String text) {
            this.builder = builder;
            format("text", text);
        }

        public Font color(String hexColor) {
            format("color", !StringUtils.hasText(hexColor) ? "#FFFFFF" : hexColor);
            return this;
        }

        public Font face(String fontFace) {
            format("face", !StringUtils.hasText(fontFace) ? fontFace : "monospace");
            return this;
        }

        public Builder builder() {
            correctTemp();
            builder.fullContent.add(temp);
            return builder;
        }

        private void correctTemp() {
            temp = temp.replace("{face}", "monospace").replace("{color}", "#FFFFFF");
        }

        private void format(String mark, String replacement) {
            temp = temp.replace("{" + mark + "}", replacement);
        }
    }
}
