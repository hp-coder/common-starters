package com.hp.dingtalk.service;

import com.dingtalk.api.response.OapiMediaUploadResponse;
import com.hp.dingtalk.pojo.file.MediaRequest;
import lombok.NonNull;

import java.io.IOException;

/**
 * @author hp 2023/3/15
 */
public interface IDingMediaHandler {

    OapiMediaUploadResponse upload(@NonNull MediaRequest mediaRequest) throws IOException;
}
