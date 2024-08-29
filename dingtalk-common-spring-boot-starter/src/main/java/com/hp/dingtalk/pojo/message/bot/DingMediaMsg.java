package com.hp.dingtalk.pojo.message.bot;

import cn.hutool.core.util.StrUtil;
import com.google.common.base.Preconditions;
import com.hp.dingtalk.service.file.media.DingMediaType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;


/**
 * @author hp
 */
public interface DingMediaMsg extends IDingBotMsg {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class SampleAudio implements DingMediaMsg {
        String mediaId;
        String duration;

        /**
         *
         * @param mediaId 通过上传媒体文件接口，获取media_id参数值。
         * @param duration 语音消息时长，单位毫秒。
         */
        public SampleAudio(String mediaId, Duration duration) {
            Preconditions.checkArgument(StrUtil.isNotEmpty(mediaId));
            Preconditions.checkArgument(Objects.nonNull(duration));
            this.mediaId = mediaId;
            this.duration = String.valueOf(duration.get(ChronoUnit.MILLIS));
        }
    }

     @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class SampleFile implements DingMediaMsg {
        String mediaId;
        String fileName;
        String fileType;

        /**
         * @param mediaId  通过上传媒体文件接口，获取media_id参数值。
         * @param fileName 文件名称。
         * @param fileType 文件类型。
         */
        public SampleFile(String mediaId, String fileName, SampleFileType fileType) {
            Preconditions.checkArgument(StrUtil.isNotEmpty(mediaId));
            Preconditions.checkArgument(StrUtil.isNotEmpty(fileName));
            Preconditions.checkArgument(Objects.nonNull(fileType));
            this.mediaId = mediaId;
            this.fileName = fileName;
            this.fileType = fileType.name();
        }

        public enum SampleFileType {
            /***/
            xlsx, pdf, zip, rar, doc, docx
        }
    }

     @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class SampleVideo implements DingMediaMsg {
        String videoMediaId;
        String videoType;
        String picMediaId;
        String duration;
        String height;
        String width;

        /**
         * @param videoMediaId 通过上传媒体文件接口，获取media_id参数值。
         * @param picMediaId   通过上传媒体文件接口，获取media_id参数值。
         * @param duration     语音消息时长，单位秒。
         * @param height       视频展示高度，单位px。
         * @param width        视频展示宽度，单位px。
         */
        public SampleVideo(
                String videoMediaId,
                String picMediaId,
                Duration duration,
                String height,
                String width
        ) {
            Preconditions.checkArgument(StrUtil.isNotEmpty(videoMediaId));
            Preconditions.checkArgument(StrUtil.isNotEmpty(picMediaId));
            Preconditions.checkArgument(Objects.nonNull(duration));
            Preconditions.checkArgument(Objects.nonNull(height));
            Preconditions.checkArgument(Objects.nonNull(width));
            this.videoMediaId = videoMediaId;
            //视频类型，支持mp4格式。
            this.videoType = DingMediaType.VIDEO.getSuffix();
            this.picMediaId = picMediaId;
            this.duration = String.valueOf(duration.get(ChronoUnit.SECONDS));
            this.height = height;
            this.width = width;
        }
    }
}
