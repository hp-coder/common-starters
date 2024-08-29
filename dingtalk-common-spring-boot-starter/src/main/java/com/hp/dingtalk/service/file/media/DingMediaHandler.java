package com.hp.dingtalk.service.file.media;

import com.dingtalk.api.request.OapiMediaUploadRequest;
import com.dingtalk.api.response.OapiMediaUploadResponse;
import com.google.common.base.Preconditions;
import com.hp.dingtalk.constant.DingUrlConstant;
import com.hp.dingtalk.service.AbstractDingApiHandler;
import com.hp.dingtalk.service.IDingMediaHandler;
import com.hp.dingtalk.component.application.IDingApp;
import com.hp.dingtalk.pojo.file.MediaRequest;
import com.taobao.api.FileItem;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author hp 2023/3/15
 */
@Slf4j
public class DingMediaHandler extends AbstractDingApiHandler implements IDingMediaHandler {

    public DingMediaHandler(@NonNull IDingApp app) {
        super(app);
    }

    @Override
    public OapiMediaUploadResponse upload(@NotNull MediaRequest request) throws IOException {
        final FileItem fileItem = request.toFileItem();
        Preconditions.checkArgument(fileItem.isValid(), "文件不合法");
        OapiMediaUploadRequest req = new OapiMediaUploadRequest();
        req.setType(request.getMediaType().name());
        req.setMedia(fileItem);
        return execute(
                DingUrlConstant.MEDIA_UPLOAD,
                req,
                () -> "上传媒体文件"
        );
    }
}
