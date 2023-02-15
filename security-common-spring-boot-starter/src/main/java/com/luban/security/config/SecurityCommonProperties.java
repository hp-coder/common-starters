package com.luban.security.config;


import com.google.common.collect.Lists;
import com.luban.common.base.annotations.FieldDesc;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author hp
 */
@Component
@Data
@ConfigurationProperties(prefix = "luban.security")
public class SecurityCommonProperties {

    @FieldDesc("token过期时间默认30天")
    private Long expired = 30 * 24 * 60L;

    @FieldDesc("是否开启权限")
    private boolean enable;

    @FieldDesc("不需要权限的链接地址集合")
    private List<String> unAuthUrls = Lists.newArrayList();

}
