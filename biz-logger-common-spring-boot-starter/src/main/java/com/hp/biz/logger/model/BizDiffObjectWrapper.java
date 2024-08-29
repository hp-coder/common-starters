package com.hp.biz.logger.model;

import com.hp.common.base.annotation.FieldDesc;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hp
 */
@AllArgsConstructor
@Getter
public class BizDiffObjectWrapper {

    @FieldDesc("修改之前的值")
    private Object initValue;

    @FieldDesc("修改之后的值")
    private Object changedValue;
}
