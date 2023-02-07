package com.hp.oss.client;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;

import java.io.IOException;
import java.io.InputStream;

/**
 * OSS客户端
 *
 * @author HP
 * @date 2022/8/25
 */
public interface OssClient {

    void createBucket(String bucketName);
    void bucketPolicy(String bucketName,String policy);
    String getObjectURL(String bucketName,String objectName);
    S3Object getObject(String bucketName,String objectName);
    PutObjectResult putObject(String bucketName, String objectName, InputStream inputStream, long size, String contentType) throws IOException;
    AmazonS3 getS3Client();
    default PutObjectResult putObject(String bucketName,String objectName,InputStream inputStream) throws IOException{
        return putObject(bucketName,objectName,inputStream,inputStream.available(),"application/octet-stream");
    }
}
