package com.hp.dingtalk.pojo.message.interactive;

import com.google.common.base.Preconditions;
import com.hp.common.base.annotation.FieldDesc;
import com.hp.dingtalk.pojo.message.IDingMsg;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Interactive card messages
 *
 * @author hp
 */
public interface IDingInteractiveMsg extends IDingMsg<Map<String, String>> {
    @FieldDesc("互动卡片全局密码盐，用于对消息对sign值加密")
    String GLOBAL_SALT = "dfaen23djf461nJ51FHDowie17hf1";

    /**
     * Using the instance id of the card, generate a md5 encrypted key.
     *
     * @param outTrackId instance id of the card
     * @return encrypted key
     */
    static String getEncryptedSign(String outTrackId) {
        Preconditions.checkNotNull(outTrackId, "参数异常：outTrackId缺失");
        final String combine = outTrackId + GLOBAL_SALT;
        return DigestUtils.md5DigestAsHex(combine.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Empty implementation
     * <p>
     * Should not be called.
     *
     * @return null
     */
    @Override
    default String getMsgType() {
        throw new IllegalStateException("The interactive card message doesn't need a msgType");
    }

    /**
     * According to the documentation and the APIs, convert the
     * message object to a map.
     *
     * @return A map of which keys are the literal fields' names,
     * and values are the fields' values converted into strings.
     */
    @Override
    default Map<String, String> getMsg() {
        final BeanMap beanMap = BeanMap.create(this);
        final Map<String, String> map = new HashMap<>(beanMap.size());
        beanMap.forEach((k, v) -> {
            if (Objects.nonNull(v)) {
                map.put(String.valueOf(k), String.valueOf(v));
            }
        });
        return map;
    }

    /**
     * Used as the identification of the card
     *
     * @return id sequence
     */
    String getOutTrackId();

    /**
     * The callback route key, directly related to the interactive card message,
     * should be configured before constructing an interactive card message.
     *
     * @return the route key.
     */
    String getCallbackRouteKey();

    /**
     * After constructing an interactive card message template
     * on the card platform, the template Id can be found in the template description
     * section.
     *
     * @return the template id of the template
     */
    String getTemplateId();
}
