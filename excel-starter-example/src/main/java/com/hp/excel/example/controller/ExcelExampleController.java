package com.hp.excel.example.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.hp.excel.annotation.ExcelSelect;
import com.hp.excel.annotation.ResponseExcel;
import com.hp.excel.annotation.Sheet;
import com.hp.excel.dto.ExcelSelectDataColumn;
import com.hp.excel.enhence.DynamicSelectDataWriterEnhance;
import com.hp.excel.enhence.handler.SelectDataSheetWriteHandler;
import com.hp.excel.example.model.ExcelExample;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author HP 2023/1/5
 */
@Slf4j
@RestController
public class ExcelExampleController {

    @ResponseExcel(
            name = "项目台账模板",
            sheets = @Sheet(sheetName = "项目模版", sheetNo = 0),
            enhancement = DynamicSelectDataWriterEnhance.class
    )
    @GetMapping("/template")
    public List<ExcelExample> template() {
        return Collections.singletonList(new ExcelExample());
    }

    @PostMapping("/easyexcel/template")
    public void template(HttpServletRequest request, HttpServletResponse response) {
        String filename = "文件名称";
        try {
            String userAgent = request.getHeader("User-Agent");
            if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
                // 针对IE或者以IE为内核的浏览器：
                filename = java.net.URLEncoder.encode(filename, "UTF-8");
            } else {
                // 非IE浏览器的处理:
                filename = new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
            }
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-disposition", String.format("attachment; filename=\"%s\"", filename + ".xlsx"));
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", -1);
            response.setCharacterEncoding("UTF-8");

            final ExcelWriterBuilder excelWriterBuilder = EasyExcel.write(response.getOutputStream());
            //点到SelectDataSheetWriteHandler里去
            excelWriterBuilder.registerWriteHandler(new SelectDataSheetWriteHandler(resolveExcelSelect(ExcelExample.class)));
            ExcelWriter excelWriter = excelWriterBuilder.build();
            WriteSheet writeSheet = EasyExcel
                    .writerSheet(0, "sheet名称")
                    .head(ExcelExample.class)
                    .build();
            excelWriter.write(new ArrayList<String>(), writeSheet);
            excelWriter.finish();
        } catch (UnsupportedEncodingException e) {
            log.error("导出Excel编码异常", e);
        } catch (IOException e) {
            log.error("导出Excel文件异常", e);
        }
    }

    public static <T> Map<Integer, ExcelSelectDataColumn> resolveExcelSelect(Class<T> dataClass) {
        Map<Integer, ExcelSelectDataColumn> selectedMap = new HashMap<>(16);
        Field[] fields = dataClass.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            ExcelSelect selected = field.getAnnotation(ExcelSelect.class);
            ExcelProperty property = field.getAnnotation(ExcelProperty.class);
            if (selected != null) {
                ExcelSelectDataColumn excelSelectedResolve;
                if (StrUtil.isNotEmpty(selected.parentColumn())) {
                    excelSelectedResolve = new ExcelSelectDataColumn<Map<String, List<String>>>();
                } else {
                    excelSelectedResolve = new ExcelSelectDataColumn<List<String>>();
                }
                final Object source = excelSelectedResolve.resolveSource(selected);
                if (Objects.nonNull(source)) {
                    if (property != null) {
                        excelSelectedResolve.setParentColumn(selected.parentColumn());
                        excelSelectedResolve.setColumn(property.value()[0]);
                        excelSelectedResolve.setSource(source);
                        excelSelectedResolve.setFirstRow(selected.firstRow());
                        excelSelectedResolve.setLastRow(selected.lastRow());
                        int index = property.index();
                        if (index >= 0) {
                            selectedMap.put(index, excelSelectedResolve);
                        } else {
                            index = i;
                            selectedMap.put(index, excelSelectedResolve);
                        }
                        excelSelectedResolve.setColumnIndex(index);
                    }
                }
            }
        }
        if (CollUtil.isNotEmpty(selectedMap)) {
            final Map<String, Integer> indexMap = selectedMap.values().stream().collect(Collectors.toMap(ExcelSelectDataColumn::getColumn, ExcelSelectDataColumn::getColumnIndex));
            selectedMap.forEach((k, v) -> {
                if (indexMap.containsKey(v.getParentColumn())) {
                    v.setParentColumnIndex(indexMap.get(v.getParentColumn()));
                }
            });
        }
        return selectedMap;
    }
}
