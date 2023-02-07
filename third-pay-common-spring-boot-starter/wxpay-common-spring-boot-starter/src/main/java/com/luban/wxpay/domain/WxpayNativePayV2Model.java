package com.luban.wxpay.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * @author HP 2022/11/24
 */
@Getter
@Setter
public class WxpayNativePayV2Model extends WxpayObject {
    private static final long serialVersionUID = -3774352966950883802L;

    private String appid;
    private String mch_id;
    private String sub_appid;
    private String sub_mch_id;
    private String device_info;
    private String nonce_str;
    private String sign;
    private String sign_type;
    private String body;
    private String detail;
    private String attach;
    private String out_trade_no;
    private String fee_type;
    private int total_fee;
    private String spbill_create_ip;
    private String time_start;
    private String time_expire;
    private String goods_tag;
    private String notify_url;
    private String trade_type;
    private String product_id;
    private String limit_pay;
    private String openid;
    private String sub_openid;
    private String receipt;
    private String scene_info;
    private String profit_sharing;


    public WxpayNativePayV2Model(String appid, String mch_id, String nonce_str, String body, String outTradeNo, int totalFee, String spBillCreateIp, String notifyUrl, String tradeType) {
        this.body = body;
        this.out_trade_no = outTradeNo;
        this.total_fee = totalFee;
        this.spbill_create_ip = spBillCreateIp;
        this.notify_url = notifyUrl;
        this.trade_type = tradeType;
    }

}
