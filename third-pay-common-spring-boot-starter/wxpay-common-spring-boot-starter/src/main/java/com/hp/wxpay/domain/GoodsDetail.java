package com.hp.wxpay.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

/**
 * @author hp
 */
@Getter
@Setter
public class GoodsDetail {
    @JsonAlias("merchant_goods_id")
    private String merchantGoodsId;
    @JsonAlias("wechatpay_goods_id")
    private String wechatpayGoodsId;
    @JsonAlias("goods_name")
    private String goodsName;
    @JsonAlias("quantity")
    private Integer quantity;
    @JsonAlias("unit_price")
    private Integer unitPrice;

    private GoodsDetail() {
    }

    public GoodsDetail(String merchantGoodsId) {
        this.merchantGoodsId = merchantGoodsId;
    }
}
