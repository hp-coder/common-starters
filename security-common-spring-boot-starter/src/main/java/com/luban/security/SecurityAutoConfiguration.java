package com.luban.security;

import com.luban.security.config.SecurityConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author HP
 * @date 2022/10/21
 */
@Configuration
@ConditionalOnProperty(prefix = "luban.security", name = "enable", havingValue = "true")
public class SecurityAutoConfiguration {

    @Configuration
    @ComponentScan(value = {"com.luban.security.base","com.luban.security.config"})
    @Import(value = {SecurityConfig.class})
    public static class AdminSecurityConfig{

    }
}
