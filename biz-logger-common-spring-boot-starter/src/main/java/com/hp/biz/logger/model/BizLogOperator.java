package com.hp.biz.logger.model;

import com.hp.common.base.annotation.FieldDesc;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hp
 */
@Getter
@AllArgsConstructor
public class BizLogOperator {

    @FieldDesc("操作人唯一标识")
    private String operatorId;

    @FieldDesc("操作人名称")
    private final String operatorName;

    public BizLogOperator(String operatorName) {
        this.operatorName = operatorName;
    }

    public static BizLogOperator emptyOperator() {
        return new BizLogOperator("", "");
    }
}
