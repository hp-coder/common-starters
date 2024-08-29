package com.hp.dingtalk.pojo.callback;

import com.hp.common.base.model.Request;
import lombok.Data;

/**
 * @author hp
 */
@Data
public class DingMiniH5EventCallbackRequest implements Request {
    private String signature;
    private String timestamp;
    private String nonce;
}
