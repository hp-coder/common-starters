package com.hp.alipay.config;

import com.alipay.api.AlipayConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 * @author HP 2022/11/14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "alipay")
public class AlipayClientConfig extends AlipayConfig implements Serializable {
    private static final long serialVersionUID = 7746869302066254918L;

    /**
     * 全局配置是否使用证书方式请求*
     */
    private boolean globalCertificatedRequest = false;

    /**
     * 是否使用默认controller*
     */
    private boolean defaultControllerEnabled = false;

    /**
     * 支付事件通知接口*
     */
    private String notifyUrl;
}

