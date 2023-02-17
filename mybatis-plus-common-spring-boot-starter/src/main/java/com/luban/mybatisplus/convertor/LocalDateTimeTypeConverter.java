package com.luban.mybatisplus.convertor;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author hp 2023/2/17
 */
public class LocalDateTimeTypeConverter implements TypeHandler<LocalDateTime> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void setParameter(PreparedStatement ps, int i, LocalDateTime parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.format(FORMATTER));
    }

    @Override
    public LocalDateTime getResult(ResultSet rs, String columnName) throws SQLException {
        return LocalDateTime.parse(rs.getString(columnName), FORMATTER);
    }

    @Override
    public LocalDateTime getResult(ResultSet rs, int columnIndex) throws SQLException {
        return LocalDateTime.parse(rs.getString(columnIndex), FORMATTER);
    }

    @Override
    public LocalDateTime getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return LocalDateTime.parse(cs.getString(columnIndex), FORMATTER);
    }
}
