package com.hp.mybatisplus.converter;

import cn.hutool.core.date.DatePattern;
import com.hp.mybatisplus.annotation.Converter;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * @author hp
 */
@Converter
public class LocalDateTimeTypeConverter implements TypeHandlerAdapter<LocalDateTime, String> {

    protected DateTimeFormatter formatter = DatePattern.NORM_DATETIME_FORMATTER;

    @Override
    public void setParameter(PreparedStatement ps, int i, LocalDateTime parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, fieldToColumn(parameter));
    }

    @Override
    public LocalDateTime getResult(ResultSet rs, String columnName) throws SQLException {
        return columnToField(rs.getString(columnName));
    }

    @Override
    public LocalDateTime getResult(ResultSet rs, int columnIndex) throws SQLException {
        return columnToField(rs.getString(columnIndex));
    }

    @Override
    public LocalDateTime getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return columnToField(cs.getString(columnIndex));
    }

    @Override
    public String fieldToColumn(LocalDateTime field) {
        return Optional.ofNullable(field).map(i -> i.format(formatter)).orElse(null);
    }

    @Override
    public LocalDateTime columnToField(String column) {
        return Optional.ofNullable(column).map(i -> LocalDateTime.parse(i, formatter)).orElse(null);
    }
}
