package com.hp.sync.handler;

import com.alibaba.fastjson.JSON;
import com.hp.sync.SyncMessage;
import com.hp.sync.support.Constants;
import com.hp.sync.support.DingMsgUtils;
import com.luban.dingding.pojo.message.DingMarkdownMsg;
import com.luban.dingding.utils.DingMarkdown;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 表数据同步处理器
 * 实现接口并实现对应DML方法完成对同步数据的对应处理
 *
 * @Author: HP
 */
@Slf4j
public abstract class AbstractSyncHandler implements SyncHandler {

    @Override
    public void handle(SyncMessage syncMessage) {
        try {
            final Constants.DML type = syncMessage.getType();
            if (Constants.DML.TRUNCATE.equals(type) ||
                    Constants.DML.RENAME.equals(type)) {
                handleTruncate(syncMessage);
            } else if (Constants.DML.DELETE.equals(type)) {
                handleDelete(syncMessage);
            } else {
                handleSave(syncMessage);
                afterSaved(syncMessage);
            }
        } catch (Exception e) {
            log.error("同步异常：{}", JSON.toJSONString(syncMessage), e);
            final String stackTrace = Arrays.stream(e.getStackTrace())
                    .limit(100)
                    .map(StackTraceElement::toString)
                    .collect(Collectors.joining("  \n  "));
            final String build = DingMarkdown.builder()
                    .level2Title("同步消息")
                    .text(JSON.toJSONString(syncMessage))
                    .level2Title("异常")
                    .text(stackTrace).build();
            DingMsgUtils.sendMsgByMobile(new DingMarkdownMsg(new DingMarkdownMsg.SampleMarkdown("数据同步异常", build)));
            throw new RuntimeException("同步处理器异常，已通过钉钉机器人发送");
        }
    }

    protected void handleTruncate(SyncMessage syncMessage) {

    }

    protected void handleSave(SyncMessage syncMessage) {
        if (CollectionUtils.isEmpty(syncMessage.getPkValues())) {
            return;
        }
        doSave(syncMessage);
    }

    public abstract void doSave(SyncMessage syncMessage);

    protected void handleDelete(SyncMessage syncMessage) {
        if (CollectionUtils.isEmpty(syncMessage.getPkValues())) {
            return;
        }
        doDelete(syncMessage);
    }

    public abstract void doDelete(SyncMessage syncMessage);

    public void afterSaved(SyncMessage syncMessage) {

    }
}