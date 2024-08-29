package com.hp.dingtalk.component.factory.token;

import com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenRequest;
import com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenResponse;
import com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenResponseBody;
import com.aliyun.tea.TeaException;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.google.common.base.Preconditions;
import com.hp.dingtalk.component.IDingOldApi;
import com.hp.dingtalk.constant.DingUrlConstant;
import com.hp.dingtalk.utils.DingUtils;
import com.hp.dingtalk.component.IDingNewApi;
import com.hp.dingtalk.component.SDK;
import com.hp.dingtalk.component.application.IDingApp;
import com.hp.dingtalk.component.exception.DingApiException;
import com.taobao.api.ApiException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hp
 */
@Slf4j
public class DingAccessTokenFactory implements IDingToken, IDingOldApi, IDingNewApi {

    private DingAccessTokenFactory() {
    }

    private static class SingletonHolder {
        private static final DingAccessTokenFactory INSTANCE = new DingAccessTokenFactory();
    }

    private static final ConcurrentHashMap<String, ConcurrentHashMap<SDK.Version, DingToken>> TOKEN_CACHE = new ConcurrentHashMap<>(16);

    public static String accessToken(IDingApp dingApp) {
        log.debug("Getting accessToken with:{},usage: new sdk APIs", dingApp.getAppName());
        final DingAccessTokenFactory bean = SingletonHolder.INSTANCE;
        return bean.getAccessToken(dingApp).orElse(null);
    }

    public static String accessToken(IDingApp dingApp, SDK.Version version) {
        log.debug("Getting accessToken with:{},usage: new sdk APIs", dingApp.getAppName());
        final DingAccessTokenFactory bean = SingletonHolder.INSTANCE;
        if (Objects.equals(version, SDK.Version.NEW)) {
            return bean.getAccessToken(dingApp).orElseThrow(() -> new DingApiException("获取新版AccessToken失败"));
        }
        return bean.getAccess_token(dingApp).orElseThrow(() -> new DingApiException("获取新版AccessToken失败"));
    }

    public static String access_token(IDingApp dingApp) {
        log.debug("Getting access_token with:{},usage: old sdk APIs", dingApp.getAppName());
        final DingAccessTokenFactory bean = SingletonHolder.INSTANCE;
        return bean.getAccess_token(dingApp).orElse(null);
    }


    @Override
    public Optional<String> getAccess_token(IDingApp app) {
        Optional<String> token = checkCache(app.getAppKey(), OLD);
        if (token.isPresent()) {
            return token;
        }
        DingTalkClient client = new DefaultDingTalkClient(DingUrlConstant.AccessToken.ACCESS_TOKEN_OLD);
        OapiGettokenRequest request = new OapiGettokenRequest();
        request.setAppkey(app.getAppKey());
        request.setAppsecret(app.getAppSecret());
        request.setHttpMethod("GET");
        try {
            OapiGettokenResponse response = client.execute(request);
            DingUtils.isSuccess(response);
            final String accessToken = response.getAccessToken();
            final Long expireIn = response.getExpiresIn();
            log.debug("获取企业内部应用的access_token:{},app:{},ttl:{}", accessToken, app.getAppName(), expireIn);
            storeCache(app.getAppKey(), accessToken, expireIn, OLD, "获取企业内部应用的accessToken旧版SDK");
            return Optional.of(accessToken);
        } catch (ApiException e) {
            log.error("获取企业内部应用的access_token旧版SDK异常,应用:{}", app.getAppName(), e);
            throw new DingApiException("获取企业内部应用的access_token异常", e);
        }
    }

    @Override
    @SneakyThrows
    public Optional<String> getAccessToken(IDingApp app) {
        final String appKey = app.getAppKey();
        final String appSecret = app.getAppSecret();
        Optional<String> token = checkCache(appKey, NEW);
        if (token.isPresent()) {
            return token;
        }
        com.aliyun.dingtalkoauth2_1_0.Client client = new com.aliyun.dingtalkoauth2_1_0.Client(getConfig());
        GetAccessTokenRequest getAccessTokenRequest = new GetAccessTokenRequest()
                .setAppKey(appKey)
                .setAppSecret(appSecret);
        try {
            final GetAccessTokenResponse tokenResponse = client.getAccessToken(getAccessTokenRequest);
            final GetAccessTokenResponseBody body = tokenResponse.getBody();
            final String accessToken = body.getAccessToken();
            final Long expireIn = body.getExpireIn();
            log.debug("获取企业内部应用的accessToken:{},app:{},ttl:{}", accessToken, app.getAppName(), expireIn);
            storeCache(appKey, accessToken, expireIn, NEW, "获取企业内部应用的accessToken新版SDK");
            return Optional.of(accessToken);
        } catch (TeaException err) {
            if (StringUtils.hasText(err.code) && StringUtils.hasText(err.message)) {
                log.error("获取企业内部应用的accessToken,app:{},err:{}:{}", app.getAppName(), err.code, err.message);
            }
            throw new DingApiException(err.message, err);
        } catch (Exception e) {
            TeaException err = new TeaException(e.getMessage(), e);
            if (StringUtils.hasText(err.code) && StringUtils.hasText(err.message)) {
                log.error("获取企业内部应用的accessToken,app:{},err:{}:{}", app.getAppName(), err.code, err.message);
            }
            throw new DingApiException(err.message, e);
        }
    }

    private Optional<String> checkCache(String appKey, SDK.Version version) {
        final ConcurrentHashMap<SDK.Version, DingToken> map = TOKEN_CACHE.get(appKey);
        if (CollectionUtils.isEmpty(map) || map.get(version) == null) {
            return Optional.empty();
        }
        final DingToken token = map.get(version);
        if (token.isExpired()) {
            TOKEN_CACHE.remove(appKey);
            return Optional.empty();
        }
        return Optional.ofNullable(token.getAccessToken());
    }

    private void storeCache(String appKey, String accessToken, Long expireIn, SDK.Version version, String description) {
        if (!StringUtils.hasText(accessToken)) {
            log.debug("钉钉AccessToken:{}不存在,appKey:{},SDK.version:{}", accessToken, appKey, version);
            return;
        }
        Preconditions.checkArgument(expireIn != null && expireIn > 600L, "EXPIRED");
        ConcurrentHashMap<SDK.Version, DingToken> map = TOKEN_CACHE.get(appKey);
        if (CollectionUtils.isEmpty(map)) {
            map = new ConcurrentHashMap<>(1);
        }
        map.put(version, new DingToken(accessToken, version, LocalDateTime.now().plusSeconds(expireIn - 600), description));
        TOKEN_CACHE.put(appKey, map);
    }

    @Slf4j
    @Getter
    @Setter
    @AllArgsConstructor
    public static class DingToken {

        private String accessToken;
        private SDK.Version version;
        private LocalDateTime expiredAt;
        private String description;

        public boolean isExpired() {
            log.debug("钉钉AccessToken:{},SDK.version:{},到期时间:{},说明:{}", accessToken, version, getExpiredAt(), description);
            return !this.expiredAt.isAfter(LocalDateTime.now());
        }
    }
}
