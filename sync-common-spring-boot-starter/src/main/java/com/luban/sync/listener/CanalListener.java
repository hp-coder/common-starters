package com.luban.sync.listener;

import com.alibaba.fastjson.JSON;
import com.luban.sync.CanalMessage;
import com.luban.sync.converter.CanalConverter;
import com.luban.sync.support.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author hp
 */
@Slf4j
@RequiredArgsConstructor
@RabbitListener(queues = {Constants.MQ.CANAL_QUEUE})
public class CanalListener {

    private static final List<Integer> HEARTBEAT_CACHE = new ArrayList<>(16);
    private static final String QRTZ_SCHEDULER_STATE = "QRTZ_SCHEDULER_STATE";

    private final RabbitTemplate rabbitTemplate;

    @RabbitHandler
    public void handle(@Payload byte[] payload) {
        final CanalMessage canalMessage = CanalMessage.instance(payload);
        log.debug("canalMessage: {}", JSON.toJSONString(canalMessage));
        if (Objects.equals(canalMessage.getTable(), QRTZ_SCHEDULER_STATE)) {
            if (HEARTBEAT_CACHE.size() == 8) {
                log.info("处理Canal HEARTBEAT：表：{}, PK：{},DML：{}, HEARTBEAT:{}", canalMessage.getTable(), canalMessage.getPkNames(), canalMessage.getType(), HEARTBEAT_CACHE.size());
                HEARTBEAT_CACHE.clear();
            } else {
                HEARTBEAT_CACHE.add(1);
            }
        } else {
            log.info("处理Canal消息：表：{}, PK：{}, DML：{}", canalMessage.getTable(), canalMessage.getPkNames(), canalMessage.getType());
        }
        final String table = canalMessage.getTable();
        Optional.ofNullable(CanalConverter.converter(table))
                .map(converter -> converter.convert(canalMessage))
                .ifPresent(syncMessage -> rabbitTemplate.convertAndSend(Constants.MQ.SYNC_QUEUE, syncMessage));
    }
}
