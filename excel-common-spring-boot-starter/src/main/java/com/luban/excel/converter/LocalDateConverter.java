package com.hp.excel.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * LocalDate数据转换器*
 * @author HP
 * @date 2022/11/7
 */
@Getter
@AllArgsConstructor
public enum LocalDateConverter implements Converter<LocalDate> {
    INSTANCE;

    @Override
    public Class<?> supportJavaTypeKey() {
        return LocalDate.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public LocalDate convertToJavaData(ReadCellData cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws ParseException {
        if (contentProperty != null && contentProperty.getDateTimeFormatProperty() != null) {
            return LocalDate.parse(cellData.getStringValue(), DateTimeFormatter.ofPattern(contentProperty.getDateTimeFormatProperty().getFormat()));
        } else {
            return LocalDate.parse(cellData.getStringValue());
        }
    }

    @Override
    public WriteCellData<String> convertToExcelData(LocalDate value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        DateTimeFormatter formatter;
        if (contentProperty != null && contentProperty.getDateTimeFormatProperty() != null) {
            formatter = DateTimeFormatter.ofPattern(contentProperty.getDateTimeFormatProperty().getFormat());
        } else {
            formatter = DateTimeFormatter.ISO_LOCAL_DATE;
        }
        return new WriteCellData(value.format(formatter));
    }
}
