package com.luban.alipay;

import cn.hutool.extra.spring.EnableSpringUtil;
import com.luban.alipay.config.AlipayClientConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author hp
 */
@EnableSpringUtil
@EnableConfigurationProperties({AlipayClientConfig.class})
@Configuration
public class AlipayAutoConfiguration {
}


