package com.hp.dingtalk.component;

/**
 * 钉钉api顶层接口
 * IDing开头，Old代表旧版SDK，没有Old为新版，后续仔细区分
 * @author hp
 */
public interface IDingApi {

    /**
     * please check this out before calling any api
     */
    String API_INVOCATION_FREQUENCY_LIMIT = "https://open.dingtalk.com/document/isvapp-server/invocation-frequency-limit-1";

}
