package com.hp.dingtalk.pojo.file;

import cn.hutool.core.util.StrUtil;
import com.google.common.base.Preconditions;
import com.hp.dingtalk.service.file.media.DingMediaType;
import com.taobao.api.FileItem;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * @author hp 2023/3/15
 */
@Getter
public class MediaRequest {

    private final DingMediaType mediaType;
    private final String fileName;
    private InputStream inputStream;
    private File file;
    private String mimeType;

    public MediaRequest(DingMediaType mediaType, File file) {
        Preconditions.checkArgument(Objects.nonNull(mediaType));
        Preconditions.checkArgument(Objects.nonNull(file));
        this.mediaType = mediaType;
        this.file = file;
        this.fileName = this.file.getName();
    }

    public MediaRequest(DingMediaType mediaType, String fileName, InputStream inputStream) {
        Preconditions.checkArgument(Objects.nonNull(mediaType));
        Preconditions.checkArgument(StrUtil.isNotEmpty(fileName));
        Preconditions.checkArgument(Objects.nonNull(inputStream));
        this.mediaType = mediaType;
        this.fileName = fileName;
        this.inputStream = inputStream;
    }

    public MediaRequest(DingMediaType mediaType, String fileName, InputStream inputStream, String mimeType) {
        Preconditions.checkArgument(Objects.nonNull(mediaType));
        Preconditions.checkArgument(StrUtil.isNotEmpty(fileName));
        Preconditions.checkArgument(StrUtil.isNotEmpty(mimeType));
        Preconditions.checkArgument(Objects.nonNull(inputStream));
        this.mediaType = mediaType;
        this.fileName = fileName;
        this.inputStream = inputStream;
        this.mimeType = mimeType;
    }

    private void validateStream() throws IOException {
        Preconditions.checkArgument(StrUtil.isNotEmpty(fileName), "文件名不能为空");
        final String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        Preconditions.checkArgument(mediaType.getSuffix().contains(suffix), "非法文件类型" + suffix + "，请上传以下合法文件类型：" + mediaType.getSuffix());
        Preconditions.checkArgument(inputStream.available() <= mediaType.getSize(), "文件大小超过限制，合法文件大小为：" + mediaType.getSize() + "b");
    }

    private void validateFile() {
        Preconditions.checkArgument(StrUtil.isNotEmpty(fileName), "文件名不能为空");
        final String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        Preconditions.checkArgument(mediaType.getSuffix().contains(suffix), "非法文件类型" + suffix + "，请上传以下合法文件类型：" + mediaType.getSuffix());
        Preconditions.checkArgument(file.length() <= mediaType.getSize(), "文件大小超过限制，合法文件大小为：" + mediaType.getSize() + "b");
    }

    public FileItem toFileItem() throws IOException {
        if (file != null) {
            validateFile();
            return new FileItem(file);
        }
        validateStream();
        return new FileItem(fileName, inputStream, mimeType);
    }
}
