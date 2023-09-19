package com.luban.wxpay.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

/**
 * @author hp
 */
@Getter
@Setter
public class SettleInfo {
    @JsonAlias("profit_sharing")
    private Boolean profitSharing;
}
