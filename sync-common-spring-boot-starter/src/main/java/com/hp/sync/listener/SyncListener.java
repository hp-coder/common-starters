package com.hp.sync.listener;

import com.hp.sync.SyncMessage;
import com.hp.sync.handler.DefaultSyncHandler;
import com.hp.sync.handler.SyncHandler;
import com.hp.sync.support.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.Optional;

/**
 * @author HP 2022/12/7
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
        }
    }
}
