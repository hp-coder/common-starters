package com.hp.dingtalk.service;

import com.aliyun.tea.TeaException;
import com.aliyun.tea.TeaModel;
import com.aliyun.teaopenapi.Client;
import com.aliyun.teaopenapi.models.Config;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.google.gson.GsonBuilder;
import com.hp.common.base.annotation.FieldDesc;
import com.hp.dingtalk.component.IDingNewApi;
import com.hp.dingtalk.component.IDingOldApi;
import com.hp.dingtalk.component.SDK;
import com.hp.dingtalk.component.application.IDingApp;
import com.hp.dingtalk.component.exception.DingApiException;
import com.hp.dingtalk.component.factory.token.DingAccessTokenFactory;
import com.hp.dingtalk.utils.DingUtils;
import com.taobao.api.ApiException;
import com.taobao.api.TaobaoRequest;
import com.taobao.api.TaobaoResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author hp
 */
@Slf4j
public abstract class AbstractDingApiHandler implements IDingOldApi, IDingNewApi {

    @FieldDesc("不再对方法级别提供支持")
    protected final IDingApp app;

    protected AbstractDingApiHandler(@NonNull IDingApp app) {
        this.app = app;
    }

    protected <T extends TaobaoResponse> T execute(@NonNull String url, @NonNull TaobaoRequest<T> request, Supplier<String> description) {
        return execute(url, request, response -> {
            try {
                DingUtils.isSuccess(response);
            } catch (ApiException e) {
                final String des = description.get();
                log.error("{}旧版SDK响应失败", des, e);
                throw new DingApiException(String.format("%s旧版SDK响应失败", des), e);
            }
        }, description);
    }

    protected <T extends TaobaoResponse> T execute(@NonNull String url, @NonNull TaobaoRequest<T> request, Consumer<T> validator, Supplier<String> description) {
        return execute(url, request, validator, description, true);
    }

    protected <T extends TaobaoResponse> T execute(@NonNull String url, @NonNull TaobaoRequest<T> request, Consumer<T> validator, Supplier<String> description, boolean usingAccessToken) {
        DingTalkClient client = new DefaultDingTalkClient(url);
        final T response;
        try {
            if (usingAccessToken) {
                response = client.execute(request, getAccessToken(SDK.Version.OLD));
            } else {
                response = client.execute(request, app.getAppKey(), app.getAppSecret());
            }
            validator.accept(response);
        } catch (ApiException e) {
            final String des = description.get();
            log.error("{}执行旧版SDK失败", des, e);
            throw new DingApiException(String.format("%s执行旧版SDK失败", des), e);
        }
        return response;
    }

    protected <CLIENT extends Client, RESPONSE extends TeaModel> RESPONSE execute(Class<CLIENT> clazz, Function<CLIENT, RESPONSE> function, Supplier<String> description) {
        try {
            final Constructor<CLIENT> declaredConstructor = clazz.getDeclaredConstructor(Config.class);
            declaredConstructor.setAccessible(true);
            final CLIENT client = declaredConstructor.newInstance(getConfig());
            final RESPONSE response = function.apply(client);
            if (log.isDebugEnabled()) {
                log.debug("{}响应:{}", description.get(), new GsonBuilder().create().toJson(response));
            }
            return response;
        } catch (TeaException err) {
            final String des = description.get();
            log.error("{}执行新版SDK失败", des, err);
            throw new DingApiException(String.format("%s执行新版SDK失败", des), err);
        } catch (Exception e) {
            final String des = description.get();
            log.error("{}执行新版SDK异常", des, e);
            throw new DingApiException(String.format("%s执行新版SDK异常", des), e);
        }
    }

    protected String getAccessToken(SDK.Version version) {
        return DingAccessTokenFactory.accessToken(app, version);
    }
}
