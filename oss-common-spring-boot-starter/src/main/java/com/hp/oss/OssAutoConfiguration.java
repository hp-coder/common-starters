package com.hp.oss;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.hp.oss.client.OssClient;
import com.hp.oss.client.S3OssClient;
import com.hp.oss.config.OssProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 通用OSS服务
 *
 * @author hp
 * @date 2022/8/25
 */
@Configuration
@ConditionalOnProperty(prefix = "oss",name = "enable",havingValue = "true")
@EnableConfigurationProperties(OssProperties.class)
public class OssAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(S3OssClient.class)
    public OssClient ossClient(AmazonS3 amazonS3){
        return new S3OssClient(amazonS3);
    }

    @Bean
    @ConditionalOnMissingBean(AmazonS3.class)
    public AmazonS3 amazonS3(OssProperties ossProperties){
        final long count = Stream.builder()
                .add(ossProperties.getEndpoint())
                .add(ossProperties.getAccessSecret())
                .add(ossProperties.getAccessKey())
                .build()
                .filter(Objects::isNull)
                .count();
        if (count > 0) {
            throw new RuntimeException("OSS配置错误，Endpoint,secret,key配置不能为空，请检查");
        }
        final BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(ossProperties.getAccessKey(), ossProperties.getAccessSecret());
        final AWSStaticCredentialsProvider awsStaticCredentialsProvider = new AWSStaticCredentialsProvider(basicAWSCredentials);
        return AmazonS3Client.builder()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(ossProperties.getEndpoint(),ossProperties.getRegion()))
                .withCredentials(awsStaticCredentialsProvider)
                .disableChunkedEncoding()
                .withPathStyleAccessEnabled(ossProperties.isPathStyleAccess())
                .build();
    }
}
