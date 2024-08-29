package com.hp.dingtalk.service;

import lombok.NonNull;

/**
 * 下载机器人接收消息的文件内容
 *
 * @author hp 2023/3/22
 */
public interface IDingBotFileDownloadHandler {

    String downloadUrl(@NonNull String downloadCode) throws Exception;
}
