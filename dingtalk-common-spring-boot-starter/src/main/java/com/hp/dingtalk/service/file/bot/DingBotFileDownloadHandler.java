package com.hp.dingtalk.service.file.bot;

import com.aliyun.dingtalkrobot_1_0.Client;
import com.aliyun.dingtalkrobot_1_0.models.RobotMessageFileDownloadResponse;
import com.aliyun.teautil.models.RuntimeOptions;
import com.hp.dingtalk.service.AbstractDingApiHandler;
import com.hp.dingtalk.service.IDingBotFileDownloadHandler;
import com.hp.dingtalk.component.SDK;
import com.hp.dingtalk.component.application.IDingBot;
import com.hp.dingtalk.component.exception.DingApiException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * @author hp 2023/3/22
 */
@Slf4j
public class DingBotFileDownloadHandler extends AbstractDingApiHandler implements IDingBotFileDownloadHandler {
    public DingBotFileDownloadHandler(IDingBot app) {
        super(app);
    }

    @Override
    public String downloadUrl(@NonNull String downloadCode) {
        com.aliyun.dingtalkrobot_1_0.models.RobotMessageFileDownloadHeaders headers =
                new com.aliyun.dingtalkrobot_1_0.models.RobotMessageFileDownloadHeaders();
        headers.xAcsDingtalkAccessToken = getAccessToken(SDK.Version.NEW);
        com.aliyun.dingtalkrobot_1_0.models.RobotMessageFileDownloadRequest request =
                new com.aliyun.dingtalkrobot_1_0.models.RobotMessageFileDownloadRequest()
                        .setDownloadCode(downloadCode)
                        .setRobotCode(app.getAppKey());
        final RobotMessageFileDownloadResponse response = execute(
                Client.class,
                client -> {
                    try {
                        return client.robotMessageFileDownloadWithOptions(request, headers, new RuntimeOptions());
                    } catch (Exception e) {
                        log.error("机器人根据downloadCode获取附件url失败", e);
                        throw new DingApiException("机器人根据downloadCode获取附件url失败");
                    }
                },
                () -> "机器人根据downloadCode获取附件url"
        );
        return response.getBody().getDownloadUrl();
    }
}
