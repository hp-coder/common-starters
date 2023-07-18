package com.hp.wxpay.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

/**
 * @author HP 2022/11/23
 */
@Getter
@Setter
public class SceneInfo {
    @JsonAlias("payer_client_ip")
    private String payerClientIp;
    @JsonAlias("device_id")
    private String deviceId;
    @JsonAlias("store_info")
    private StoreInfo storeInfo;

    private SceneInfo() {
    }

    public SceneInfo(String payerClientIp) {
        this.payerClientIp = payerClientIp;
    }
}
