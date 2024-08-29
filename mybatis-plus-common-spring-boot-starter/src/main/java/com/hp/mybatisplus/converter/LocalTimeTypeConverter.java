package com.hp.mybatisplus.converter;

import cn.hutool.core.date.DatePattern;
import com.hp.mybatisplus.annotation.Converter;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * @author hp
 */
@Converter
public class LocalTimeTypeConverter implements TypeHandlerAdapter<LocalTime, String> {
    protected DateTimeFormatter formatter = DatePattern.NORM_TIME_FORMATTER;

    @Override
    public void setParameter(PreparedStatement ps, int i, LocalTime parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.format(formatter));
    }

    @Override
    public LocalTime getResult(ResultSet rs, String columnName) throws SQLException {
        return columnToField(rs.getString(columnName));
    }

    @Override
    public LocalTime getResult(ResultSet rs, int columnIndex) throws SQLException {
        return columnToField(rs.getString(columnIndex));
    }

    @Override
    public LocalTime getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return columnToField(cs.getString(columnIndex));
    }

    @Override
    public String fieldToColumn(LocalTime field) {
        return Optional.ofNullable(field).map(i -> i.format(formatter)).orElse(null);
    }

    @Override
    public LocalTime columnToField(String column) {
        return Optional.ofNullable(column).map(i -> LocalTime.parse(i, formatter)).orElse(null);
    }
}
