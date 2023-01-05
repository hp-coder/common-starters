package com.hp.excel.example.controller;

import com.hp.excel.annotation.ResponseExcel;
import com.hp.excel.annotation.Sheet;
import com.hp.excel.enhence.DynamicSelectDataWriterEnhance;
import com.hp.excel.example.model.ExcelExample;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 * @author HP 2023/1/5
 */
@RestController
public class ExcelExampleController {

    @ResponseExcel(
            name = "项目台账模板",
            sheets = @Sheet(sheetName = "项目模版", sheetNo = 0),
            enhancement = DynamicSelectDataWriterEnhance.class
    )
    @PostMapping("/template")
    public List<ExcelExample> template() {
        System.out.println("===============");
        System.out.println("===============");
        System.out.println("===============");
        return Collections.singletonList(new ExcelExample());
    }
}
