package com.hp.mybatisplus.convertor;

import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * @author hp 2023/3/31
 */
public class LocalDateTypeConverter implements TypeHandlerCodeGenAdapter<LocalDate, String> {

    @Override
    public void setParameter(PreparedStatement ps, int i, LocalDate parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.format(DateTimeFormatter.ISO_DATE));
    }

    @Override
    public LocalDate getResult(ResultSet rs, String columnName) throws SQLException {
        final String data = rs.getString(columnName);
        return Optional.ofNullable(data).map(i -> LocalDate.parse(i, DateTimeFormatter.ISO_DATE)).orElse(null);
    }

    @Override
    public LocalDate getResult(ResultSet rs, int columnIndex) throws SQLException {
        final String data = rs.getString(columnIndex);
        return Optional.ofNullable(data).map(i -> LocalDate.parse(i, DateTimeFormatter.ISO_DATE)).orElse(null);
    }

    @Override
    public LocalDate getResult(CallableStatement cs, int columnIndex) throws SQLException {
        final String data = cs.getString(columnIndex);
        return Optional.ofNullable(data).map(i -> LocalDate.parse(i, DateTimeFormatter.ISO_DATE)).orElse(null);
    }
}
