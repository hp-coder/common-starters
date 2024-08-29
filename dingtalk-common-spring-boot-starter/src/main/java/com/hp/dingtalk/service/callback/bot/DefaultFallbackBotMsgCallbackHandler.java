package com.hp.dingtalk.service.callback.bot;

import com.hp.dingtalk.service.IDingBotMsgCallBackHandler;
import com.hp.dingtalk.component.application.IDingBot;
import com.hp.dingtalk.pojo.callback.DingBotMsgCallbackRequest;
import com.hp.dingtalk.pojo.message.bot.IDingBotMsg;
import com.hp.dingtalk.pojo.message.bot.DingMarkdownMsg;
import com.hp.dingtalk.utils.DingMarkdown;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 兜底处理器
 * <p>
 * 如果没有一个正常处理器能处理，那么将使用该处理器返回信息
 *
 * @author hp
 */
@RequiredArgsConstructor
public class DefaultFallbackBotMsgCallbackHandler implements IDingBotMsgCallBackHandler<String> {

    private final ApplicationContext applicationContext;

    @Override
    public Predicate<DingBotMsgCallbackRequest> predication() {
        return null;
    }

    @Override
    public IDingBotMsg message(IDingBot app, DingBotMsgCallbackRequest payload, String data) {
        final DingMarkdown.Builder builder = DingMarkdown.builder()
                .contentTitle("默认处理器")
                .level2Title("说明")
                .text("未找到对应处理器，请检查发送内容（关键字，格式，长度等）或联系开发人员处理！")
                .newLine()
                .level3Title("请求原文")
                .textWithFont("'" + payload.getText().getContent() + "'").color("#67C23A").builder()
                .newLine()
                .level3Title("已有处理器")
                .disorderedList(this.getHandlerDescriptions().toArray(new String[0]));
        return new DingMarkdownMsg.SampleMarkdown("消息说明", builder);
    }

    @Override
    public String beforeMessageSend(IDingBot app, DingBotMsgCallbackRequest payload) {
        return null;
    }

    private List<String> getHandlerDescriptions() {
        final Collection<IDingBotMsgCallBackHandler> values = applicationContext.getBeansOfType(IDingBotMsgCallBackHandler.class).values();
        if (CollectionUtils.isEmpty(values)) {
            return Collections.singletonList("未配置任何处理器");
        }
        return values.stream()
                .map(IDingBotMsgCallBackHandler::description)
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());
    }

    @Override
    public Integer order() {
        return Integer.MAX_VALUE;
    }
}
