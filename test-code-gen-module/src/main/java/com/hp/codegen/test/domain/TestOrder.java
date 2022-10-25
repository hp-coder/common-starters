package com.hp.codegen.test.domain;

import com.hp.codegen.processor.vo.GenVo;
import com.hp.codegen.test.domain.vo.TestOrderVO;
import lombok.Data;

import java.io.Serializable;

/**
 * @author HP
 * @date 2022/10/24
 */
@GenVo(pkgName = "com.hp.codegen.test.domain.vo")
@Data
public class TestOrder implements Serializable {
    private String skuName;

    private Long templateId;

    private String code;

    private String remark;

    private String taxCategoryNo;

    private String measureUnit;

    private TestOrderVO testOrderVO;
}
