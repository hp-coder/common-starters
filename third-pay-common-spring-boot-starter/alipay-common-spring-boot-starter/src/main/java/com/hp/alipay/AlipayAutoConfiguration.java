package com.hp.alipay;

import cn.hutool.extra.spring.EnableSpringUtil;
import com.hp.alipay.config.AlipayClientConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author HP 2022/11/11
 */
@EnableSpringUtil
@EnableConfigurationProperties({AlipayClientConfig.class})
@Configuration
public class AlipayAutoConfiguration {
}


