package com.hp.dingtalk.service.file.media;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hp 2023/3/15
 */
@Getter
@AllArgsConstructor
public enum DingMediaType {
    /**
     * 文件类型
     */
    IMAGE("image", "jpg,gif,png,bmp", 1024 * 1024),
    FILE("file", "doc,docx,xls,xlsx,ppt,pptx,zip,pdf,rar", 10 * 1024 * 1024),
    VOICE("voice", "amr,mp3,wav", 2 * 1024 * 1024),
    VIDEO("video", "mp4", 10 * 1024 * 1024),
    ;

    private final String code;
    private final String suffix;
    /**
     * bit
     */
    private final long size;
}
