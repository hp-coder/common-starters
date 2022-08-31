package com.hp.oss.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * OSS配置信息
 *
 * @author HP
 * @date 2022/8/25
 */
@ConfigurationProperties(prefix = "oss")
@Setter
@Getter
public class OssProperties {

    private boolean enable = false;
    private String accessKey;
    private String accessSecret;
    /**
     * 如果是服务器MinIO等直接使用 [$schema]://[$ip]:[$port]
     * 外网[$Schema]://[$Bucket].[$Endpoint]/[$Object]*
     * https://help.aliyun.com/document_detail/375241.html*
     */
    private String endpoint;
    /**
     * refres to com.amazonaws.regions.Regions*
     * https://help.aliyun.com/document_detail/31837.htm?spm=a2c4g.11186623.0.0.695178eb0nD6jp*
     */
    private String region;
    private boolean pathStyleAccess = true;
}
