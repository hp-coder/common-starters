package com.hp.security.config;


import com.google.common.collect.Lists;
import com.hp.common.base.annotations.FieldDesc;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author hp
 */

@Data
@ConfigurationProperties(prefix = "hp.security")
public class SecurityCommonProperties {

    @FieldDesc("token过期时间默认30天")
    private Long tokenExpired = 30 * 24 * 60L;

    @FieldDesc("token密钥")
    private String tokenSecret;

//    @FieldDesc("是否开启权限")
//    private boolean enable;

    @FieldDesc("不需要权限的链接地址集合")
    private List<String> unAuthUrls = Lists.newArrayList();

    @FieldDesc("各种登录地址集合")
    private List<String> unAuthLoginUrls = Lists.newArrayList();

}
