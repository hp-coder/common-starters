package com.hp.alipay.support;

import com.hp.common.base.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 如果退款查询发起时间早于退款时间，或者间隔退款发起时间太短，
 * * 可能出现退款查询时还没处理成功，后面又处理成功的情况，
 * * 建议商户在退款发起后间隔10秒以上再发起退款查询请求。*
 *
 * @author hp
 */
@Getter
@AllArgsConstructor
public enum AlipayRefundStatus implements BaseEnum<AlipayRefundStatus,String> {
    REFUND_SUCCESS("REFUND_SUCCESS", "退款处理成功"),
    ;
    private String code;
    private String name;
}
