package com.hp.dingtalk.component.factory.token;


import com.hp.dingtalk.component.application.IDingApp;
import com.hp.dingtalk.component.IDingApi;

import java.util.Optional;

/**
 * 钉钉accessToken顶层接口
 * 为了方便对应文档，这里的方法名还是和文档相同，但是调用是必须区分版本问题
 * @author hp
 */
public interface IDingToken extends IDingApi {

    /**
     * 获取企业内部应用的accessToken新版SDK
     * @param app 企业内部应用
     * @return token
     */
    Optional<String> getAccessToken(IDingApp app);

    /**
     * 获取企业内部应用的accessToken旧版SDK
     * @param app 企业内部应用
     * @return token
     */
    Optional<String> getAccess_token(IDingApp app);
}
