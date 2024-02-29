package com.hp.mybatisplus.convertor;

import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * @author hp 2023/3/31
 */
public class LocalTimeTypeConverter implements TypeHandlerCodeGenAdapter<LocalTime, String> {
    protected String format = "HH:mm:ss";

    @Override
    public void setParameter(PreparedStatement ps, int i, LocalTime param, JdbcType jdbcType) throws SQLException {
        ps.setString(i, param.format(DateTimeFormatter.ofPattern(format)));
    }

    @Override
    public LocalTime getResult(ResultSet rs, String columnName) throws SQLException {
        final String data = rs.getString(columnName);
        return Optional.ofNullable(data).map(i -> LocalTime.parse(data, DateTimeFormatter.ofPattern(format))).orElse(null);
    }

    @Override
    public LocalTime getResult(ResultSet rs, int columnIndex) throws SQLException {
        final String data = rs.getString(columnIndex);
        return Optional.ofNullable(data).map(i -> LocalTime.parse(data, DateTimeFormatter.ofPattern(format))).orElse(null);
    }

    @Override
    public LocalTime getResult(CallableStatement cs, int columnIndex) throws SQLException {
        final String data = cs.getString(columnIndex);
        return Optional.ofNullable(data).map(i -> LocalTime.parse(data, DateTimeFormatter.ofPattern(format))).orElse(null);
    }
}
