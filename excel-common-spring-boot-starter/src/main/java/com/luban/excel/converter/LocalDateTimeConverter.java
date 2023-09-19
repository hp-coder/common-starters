package com.luban.excel.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * LocalDateTime转换器*
 * @author hp
 * @date 2022/11/7
 */
@Getter
@AllArgsConstructor
public enum LocalDateTimeConverter implements Converter<LocalDateTime> {
    INSTANCE;

    private static final String DASH = "-";

    @Override
    public Class<?> supportJavaTypeKey() {
        return LocalDateTime.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public LocalDateTime convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        final String stringValue = cellData.getStringValue();
        String format;
        if (contentProperty != null && contentProperty.getDateTimeFormatProperty() != null) {
            format = contentProperty.getDateTimeFormatProperty().getFormat();
        } else {
            format = findWithLength(stringValue);
        }
        return LocalDateTime.parse(cellData.getStringValue(), DateTimeFormatter.ofPattern(format));
    }

    @Override
    public WriteCellData<?> convertToExcelData(LocalDateTime value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        String format;
        if (contentProperty != null && contentProperty.getDateTimeFormatProperty() != null) {
            format = contentProperty.getDateTimeFormatProperty().getFormat();
        } else {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        return new WriteCellData(value.format(DateTimeFormatter.ofPattern(format)));
    }

    private static String findWithLength(String dateString) {
        int length = dateString.length();
        switch (length) {
            case 10:
                return "yyyy-MM-dd";
            case 11:
            case 12:
            case 13:
            case 15:
            case 16:
            case 18:
            default:
                throw new IllegalArgumentException("can not find date format for：" + dateString);
            case 14:
                return "yyyyMMddHHmmss";
            case 17:
                return "yyyyMMdd HH:mm:ss";
            case 19:
                return dateString.contains(DASH) ? "yyyy-MM-dd HH:mm:ss" : "yyyy/MM/dd HH:mm:ss";
        }
    }
}
