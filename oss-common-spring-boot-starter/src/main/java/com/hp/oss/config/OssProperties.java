package com.hp.oss.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * OSS配置信息
 *
 * @author hp
 * @date 2022/8/25
 */
@ConfigurationProperties(prefix = "oss")
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
    private String bucketName;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getAccessSecret() {
        return accessSecret;
    }

    public void setAccessSecret(String accessSecret) {
        this.accessSecret = accessSecret;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public boolean isPathStyleAccess() {
        return pathStyleAccess;
    }

    public void setPathStyleAccess(boolean pathStyleAccess) {
        this.pathStyleAccess = pathStyleAccess;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }
}
