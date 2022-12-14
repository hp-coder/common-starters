package com.hp.sync.handler;

import com.alibaba.fastjson.JSON;
import com.hp.sync.SyncMessage;
import com.hp.sync.support.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

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
            throw new RuntimeException("同步处理器异常", e);
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