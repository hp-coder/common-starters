package com.hp.dingtalk.service.callback.bot;


import com.hp.dingtalk.service.IDingBotMsgCallBackHandler;
import com.hp.dingtalk.component.application.IDingBot;
import com.hp.dingtalk.pojo.callback.DingBotMsgCallbackRequest;
import com.hp.dingtalk.pojo.message.bot.IDingBotMsg;
import com.hp.dingtalk.pojo.message.bot.DingMarkdownMsg;
import com.hp.dingtalk.utils.DingMarkdown;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * @author hp
 */
public class DefaultBotMsgCallbackHandler implements IDingBotMsgCallBackHandler<String> {

    private static final Pattern PATTERN = Pattern.compile("^(?i)test$|^测试$");

    @Override
    public String beforeMessageSend(IDingBot app, DingBotMsgCallbackRequest payload) {
        return "前置处理完成：返回测试数据";
    }

    @Override
    public Predicate<DingBotMsgCallbackRequest> predication() {
        return payload -> PATTERN.asPredicate().test(payload.getText().getContent());
    }

    @Override
    public IDingBotMsg message(IDingBot bot, DingBotMsgCallbackRequest payload, String data) {
        final DingMarkdown.Builder builder = DingMarkdown.builder()
                .level3Title("测试回复")
                .text("AppName：" + bot.getAppName())
                .text("AppId：" + bot.getAppId())
                .text("Data：" + data)
                .reference(LocalDateTime.now().atZone(ZoneId.of("Asia/Shanghai")).format(DateTimeFormatter.ofPattern("MM-dd HH:mm")));
        return new DingMarkdownMsg.SampleMarkdown("测试", builder);
    }

    @Override
    public String description() {
        return "测试处理器：用于检测配置是否成功，输入\"测试\"或\"test\"查看";
    }

    @Override
    public Integer order() {
        return Integer.MAX_VALUE;
    }
}
