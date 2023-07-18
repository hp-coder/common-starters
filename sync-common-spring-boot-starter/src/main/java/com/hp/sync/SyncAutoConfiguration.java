package com.hp.sync;

import com.hp.sync.config.SyncConfig;
import com.hp.sync.listener.CanalListener;
import com.hp.sync.listener.SyncListener;
import com.hp.sync.support.Constants;
import com.hp.sync.support.DingMsgUtils;
import com.hp.dingtalk.component.application.IDingBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author HP 2022/12/7
 */
@Slf4j
@Configuration
@Import(value = {DingMsgUtils.class})
@EnableConfigurationProperties({SyncConfig.class})
public class SyncAutoConfiguration {

    @Bean
    public CanalListener canalListener(final RabbitTemplate rabbitTemplate) {
        return new CanalListener(rabbitTemplate);
    }

    @Bean
    public SyncListener syncListener() {
        return new SyncListener();
    }

    @Bean
    public IDingBot dingBot(SyncConfig syncConfig) {
        return new IDingBot() {
            @Override
            public String getCorpName() {
                return null;
            }

            @Override
            public String getCorpId() {
                return null;
            }

            @Override
            public String getAppName() {
                return syncConfig.getDingTalk().getAppName();
            }

            @Override
            public String getAppKey() {
                return syncConfig.getDingTalk().getAppKey();
            }

            @Override
            public String getAppSecret() {
                return syncConfig.getDingTalk().getAppSecret();
            }

            @Override
            public Long getAppId() {
                return syncConfig.getDingTalk().getAppId();
            }
        };
    }

    @Bean
    public DirectExchange syncExchange() {
        return new DirectExchange(Constants.MQ.SYNC_EXCHANGE);
    }

    @Bean
    public DirectExchange canalExchange() {
        return new DirectExchange(Constants.MQ.CANAL_EXCHANGE);
    }

    @Bean
    public Queue syncQueue() {
        return new Queue(Constants.MQ.SYNC_QUEUE);
    }

    @Bean
    public Queue canalQueue() {
        return new Queue(Constants.MQ.CANAL_QUEUE);
    }

    @Bean
    public Binding syncBinding(DirectExchange syncExchange, Queue syncQueue) {
        return BindingBuilder.bind(syncQueue).to(syncExchange)
                .with(Constants.MQ.SYNC_ROUTING);
    }

    @Bean
    public Binding processBinding(DirectExchange canalExchange, Queue canalQueue) {
        return BindingBuilder.bind(canalQueue).to(canalExchange)
                .with(Constants.MQ.CANAL_ROUTING);
    }
}
