package com.hp.codegen.test.domain;

import com.hp.codegen.processor.vo.GenVo;
import com.hp.codegen.test.domain.vo.TestOrderVO;
import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;

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

    public static void main(String[] args) {
        final char[] data = {'1','2'};
        char[] same = data;
        final char[] back = Arrays.copyOf(data, data.length);
        System.out.println("data = " + data);
        System.out.println("same = " + same);
        System.out.println("back = " + back);
        System.out.println("data = " + Arrays.toString(data));
        System.out.println("back = " + Arrays.toString(back));
        data[0] = '3';
        System.out.println("data = " + Arrays.toString(data));
        System.out.println("back = " + Arrays.toString(back));
    }
}
