package com.luban.wxpay.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

/**
 * @author HP 2022/11/23
 */
@Getter
@Setter
public class WxpayNativePayV3Model extends WxpayObject {

    private static final long serialVersionUID = -4730280139625667677L;
    @JsonAlias("app_id")
    private String appId;

    @JsonAlias("mch_id")
    private String mchId;

    @JsonAlias("description")
    private String description;

    @JsonAlias("out_trade_no")
    private String outTradeNo;

    @JsonAlias("time_expire")
    private String timeExpire;

    @JsonAlias("attach")
    private String attach;

    @JsonAlias("notify_url")
    private String notifyUrl;

    @JsonAlias("goods_tag")
    private String goodsTag;

    @JsonAlias("support_fapiao")
    private boolean supportFapiao;

    @JsonAlias("amount")
    private PayAmount amount;

    @JsonAlias("detail")
    private PayDetail detail;

    @JsonAlias("scene_info")
    private SceneInfo sceneInfo;

    @JsonAlias("settle_info")
    private SettleInfo settleInfo;


    private WxpayNativePayV3Model() {
    }

    public WxpayNativePayV3Model(String appId, String mchId, String description, String outTradeNo, String notifyUrl, PayAmount amount) {
        //TODO 后期在发送前处理
        this.appId = "appId";
        this.mchId = "mchId";
        this.description = description;
        this.outTradeNo = outTradeNo;
        this.notifyUrl = notifyUrl;
        this.amount = amount;
    }
}
