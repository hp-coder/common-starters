package com.hp.dingtalk.pojo.message;

/**
 * DingTalk message design according to the documentation.
 *
 * @author hp
 */
public interface IDingMsg<T> {

    /**
     * Based on the DingTalk documentation, the actual request body usually has a field called msgType,
     * but this field is implemented poorly in many APIs due to the different formats it has.
     * <p>
     * Implementation of this interface should consider overriding this method.
     *
     * @return the literal key of the concept msgType
     */
    String getMsgType();

    /**
     * A message formatted as requested by the API according to the documentation.
     * Based on the DingTalk documentation, the message is usually converted into a JSON.
     * <p>
     * Implementation of this interface should override this method.
     *
     * @return formatted message
     */
    T getMsg();
}
