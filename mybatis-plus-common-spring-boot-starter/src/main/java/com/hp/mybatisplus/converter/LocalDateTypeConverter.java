package com.hp.mybatisplus.converter;

import cn.hutool.core.date.DatePattern;
import com.hp.mybatisplus.annotation.Converter;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * @author hp
 */
@Converter
public class LocalDateTypeConverter implements TypeHandlerAdapter<LocalDate, String> {
    protected DateTimeFormatter formatter = DatePattern.NORM_DATE_FORMATTER;

    @Override
    public void setParameter(PreparedStatement ps, int i, LocalDate parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.format(formatter));
    }

    @Override
    public LocalDate getResult(ResultSet rs, String columnName) throws SQLException {
        return columnToField(rs.getString(columnName));
    }

    @Override
    public LocalDate getResult(ResultSet rs, int columnIndex) throws SQLException {
        return columnToField(rs.getString(columnIndex));
    }

    @Override
    public LocalDate getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return columnToField(cs.getString(columnIndex));
    }

    @Override
    public String fieldToColumn(LocalDate field) {
        return Optional.ofNullable(field).map(i -> i.format(formatter)).orElse(null);
    }

    @Override
    public LocalDate columnToField(String column) {
        return Optional.ofNullable(column).map(i -> LocalDate.parse(i, formatter)).orElse(null);
    }
}
