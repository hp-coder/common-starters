
package com.hp.dingtalk.service;

import com.hp.dingtalk.pojo.message.interactive.callback.IDingInteractiveCardCallBack;
import com.taobao.api.ApiException;
import lombok.NonNull;

/**
 * 发送钉钉互动卡片，高级版，普通版暂不实现
 * 以下接口都是使用钉钉userId模式，不推荐unionId模式
 * <p>
 * unionid是员工在当前开发者企业账号范围内的唯一标识，由系统生成：
 * 同一个企业员工，在不同的开发者企业账号下，unionid是不相同的。
 * 在同一个开发者企业账号下，unionid是唯一且不变的，例如同一个服务商开发的多个应用，或者是扫码登录等场景的多个App账号。
 *
 * @author hp
 */
public interface IDingInteractiveMessageCallbackRegisterHandler {

    /**
     * 注册互动卡片回调api
     *
     * @param callback    回调url配置
     * @param forceUpdate 是否强制更新，在不区分环境的情况下，强制更新，容易出现开发或测试更新到上线的问题，项目启动时默认为true，期望客户端能正确将配置通过环境区分
     * @throws ApiException 调用钉钉API异常
     */
    void registerCallBackUrl(@NonNull IDingInteractiveCardCallBack callback, boolean forceUpdate);
}
