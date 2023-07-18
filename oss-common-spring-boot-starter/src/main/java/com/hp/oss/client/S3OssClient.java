package com.hp.oss.client;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * 亚马逊s3协议客户端
 *
 * @author HP
 * @date 2022/8/25
 */
public class S3OssClient implements OssClient {

    private final AmazonS3 amazonS3;

    public S3OssClient(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    @Override
    public void createBucket(String bucketName) {
        if (amazonS3.doesBucketExistV2(bucketName)) {
            return;
        }
        amazonS3.createBucket(bucketName);
    }

    @Override
    public void bucketPolicy(String bucketName,String policy){
        amazonS3.setBucketPolicy(bucketName, policy);
    }

    @Override
    public String getObjectURL(String bucketName, String objectName) {
        final URL url = amazonS3.getUrl(bucketName, objectName);
        return url.toString();
    }

    @Override
    public S3Object getObject(String bucketName, String objectName) {
        return amazonS3.getObject(bucketName, objectName);
    }

    @Override
    public PutObjectResult putObject(String bucketName, String objectName, InputStream inputStream, long size, String contentType) throws IOException {
        ObjectMetadata metaData = new ObjectMetadata();
        metaData.setContentLength(size);
        metaData.setContentType(contentType);
        final PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream, metaData);
        putObjectRequest.getRequestClientOptions().setReadLimit((int) (size + 1));
        return amazonS3.putObject(putObjectRequest);
    }

    @Override
    public AmazonS3 getS3Client() {
        return amazonS3;
    }
}
