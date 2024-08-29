package com.hp.oss.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * OSS配置信息
 *
 * @author hp
 * @date 2022/8/25
 */
@Data
@ConfigurationProperties(prefix = "oss")
public class OssProperties {

    private boolean enable = true;

    private String accessKey;

    private String accessSecret;

    /**
     * 如果是服务器MinIO等直接使用 [$schema]://[$ip]:[$port]
     * <p>
     * 外网[$Schema]://[$Bucket].[$Endpoint]/[$Object]
     * <p>
     * <a href="https://help.aliyun.com/document_detail/375241.html">阿里云文档</a>
     */
    private String endpoint;

    /**
     * Refers to com.amazonaws.regions.Regions
     * <p>
     * <a href="https://help.aliyun.com/document_detail/31837.htm?spm=a2c4g.11186623.0.0.695178eb0nD6jp">阿里云文档</a>*
     */
    private String region;

    private boolean pathStyleAccess = true;

    private String bucketName;
}
