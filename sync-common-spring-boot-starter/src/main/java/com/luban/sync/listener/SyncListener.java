package com.luban.sync.listener;

import com.luban.sync.SyncMessage;
import com.luban.sync.handler.DefaultSyncHandler;
import com.luban.sync.handler.SyncHandler;
import com.luban.sync.support.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.Optional;

/**
 * @author hp
 */
@Slf4j
@RabbitListener(queues = Constants.MQ.SYNC_QUEUE)
public class SyncListener {

    @RabbitHandler
    public void handler(@Payload SyncMessage syncMessage) {
        log.info("处理同步消息：表：{}, PK：{}, DML：{}", syncMessage.getTable(), syncMessage.getPk(), syncMessage.getType());
        try {
            Optional.ofNullable(SyncHandler.handler(syncMessage.getTable()))
                    .orElseGet(DefaultSyncHandler::new)
                    .handle(syncMessage);
        } catch (Exception e) {
            log.error("同步处理器异常：{}", e.getLocalizedMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
