package com.hp.id;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author hp
 */
@Configuration
@Import(IdProperties.class)
@Slf4j
public class IdAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(IdHelper.class)
    public IdHelper idHelper() {
        return new IdHelper();
    }

    @Bean
    @ConditionalOnMissingBean(IdGenerator.class)
    public IdGenerator defaultIdGenerator(IdProperties idProperties) {
        return new TweeterSnowflakeBasedIdGenerator(idProperties.getWorkerId(), idProperties.getDataCenterId());
    }
}
