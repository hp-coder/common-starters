package com.hp.dingtalk.pojo.message.interactive.callback;

import com.hp.dingtalk.component.DingBooter;
import com.hp.dingtalk.component.application.IDingBot;

import java.util.List;

/**
 * Configure and register an instance of this interface
 * to the Spring Container before sending any interactive
 * card messages that need to interact with users.
 * <p>
 * Note:
 * <p>
 * Any implementations of this interface only need to be
 * registered once, so in this framework, the registration process
 * was done at the ready phase of the SpringBoot application boot-up process.
 * See: {@link DingBooter}
 *
 * @author hp
 */
public interface IDingInteractiveCardCallBack {

    /**
     * When users interact with messages, DingTalk will send POST requests to the URL.
     *
     * @return callback url
     */
    String getCallbackUrl();

    /**
     * The key is used to send with the interactive card message.
     *
     * @return route key
     */
    String getCallbackRouteKey();

    /**
     * Robot applications were used to send the interactive card message.
     *
     * @return robot application instances
     */
    List<Class<? extends IDingBot>> getDingBots();
}
