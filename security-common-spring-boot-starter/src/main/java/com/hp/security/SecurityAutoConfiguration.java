package com.hp.security;

import com.hp.security.base.extension.DummyUserContextAware;
import com.hp.security.base.extension.UserContextAware;
import com.hp.security.config.SecurityCommonProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * @author HP
 */
@EnableConfigurationProperties
@Import(SecurityCommonProperties.class)
public class SecurityAutoConfiguration {

    @ConditionalOnMissingBean(UserContextAware.class)
    @Bean
    public UserContextAware userContextAware(){
        return new DummyUserContextAware();
    }
}
