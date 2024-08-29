package com.hp.dingtalk.service.callback.minih5;

import static com.hp.dingtalk.pojo.callback.event.DingMiniH5CallbackEvents.DingMiniH5EventDecryptedPayload;

/**
 * 用于处理钉钉H5微应用订阅的事件回调
 *
 * @author hp
 */
public interface IDingMiniH5EventCallbackHandler {

    boolean support(DingMiniH5EventDecryptedPayload payload);

    boolean process(DingMiniH5EventDecryptedPayload payload);

    default int order() {return 1;}
}
