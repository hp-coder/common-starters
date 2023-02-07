package com.hp.wxpay.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;

/**
 * @author HP 2022/11/23
 */
@Getter
@Setter
public class StoreInfo {
    @JsonAlias("id")
    private String id;
    @JsonAlias("name")
    private String name;
    @JsonAlias("area_code")
    private String areaCode;
    @JsonAlias("address")
    private String address;
}
