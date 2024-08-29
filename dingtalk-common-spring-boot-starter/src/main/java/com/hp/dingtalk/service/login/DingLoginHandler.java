package com.hp.dingtalk.service.login;

import com.aliyun.dingtalkoauth2_1_0.Client;
import com.aliyun.dingtalkoauth2_1_0.models.GetUserTokenRequest;
import com.aliyun.dingtalkoauth2_1_0.models.GetUserTokenResponse;
import com.aliyun.dingtalkoauth2_1_0.models.GetUserTokenResponseBody;
import com.hp.dingtalk.service.AbstractDingApiHandler;
import com.hp.dingtalk.service.IDingLoginHandler;
import com.hp.dingtalk.component.application.IDingMiniH5;
import com.hp.dingtalk.component.exception.DingApiException;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

/**
 * @author hp 2023/3/23
 */
@Slf4j
public class DingLoginHandler extends AbstractDingApiHandler implements IDingLoginHandler {
    public DingLoginHandler(IDingMiniH5 app) {
        super(app);
    }

    @SneakyThrows(Exception.class)
    @Override
    public GetUserTokenResponseBody getUserToken(@NotNull String authCode, @NonNull GrantType grantType) {
        GetUserTokenRequest request = new GetUserTokenRequest()
                .setClientId(app.getAppKey())
                .setClientSecret(app.getAppSecret())
                .setCode(authCode)
                .setGrantType(grantType.name());

        final GetUserTokenResponse response = execute(
                Client.class,
                client -> {
                    try {
                        return client.getUserToken(request);
                    } catch (Exception e) {
                        log.error("获取用户token失败", e);
                        throw new DingApiException("获取用户token失败");
                    }
                },
                () -> "获取用户token"
        );
        return response.getBody();
    }
}
