package com.hp.dingtalk.service;

import com.hp.dingtalk.component.application.IDingCorpSSO;

/**
 *
 重要
 ISV开发的应用后台免登用的access_token，需使用ISV自己的corpId和SSOsecret来换取，不是管理员所在的企业的。
 *
 * @author hp 2023/3/22
 */
public interface IDingSSOHandler {

    String accessToken(IDingCorpSSO corpSso);
}
