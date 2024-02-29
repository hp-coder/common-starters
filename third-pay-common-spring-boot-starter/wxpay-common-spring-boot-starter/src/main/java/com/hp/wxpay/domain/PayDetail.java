package com.hp.wxpay.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author hp
 */
@Getter
@Setter
public class PayDetail {

    @JsonAlias("cost_price")
    private Integer costPrice;

    @JsonAlias("invoice_id")
    private String invoiceId;

    @JsonAlias("goods_detail")
    private List<GoodsDetail> goodsDetail;
}
