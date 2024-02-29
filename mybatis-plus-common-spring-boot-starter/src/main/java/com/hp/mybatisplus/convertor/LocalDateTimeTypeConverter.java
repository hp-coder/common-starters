package com.hp.mybatisplus.convertor;

import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * @author hp 2023/2/17
 */
public class LocalDateTimeTypeConverter implements TypeHandlerCodeGenAdapter<LocalDateTime, String> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void setParameter(PreparedStatement ps, int i, LocalDateTime parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.format(FORMATTER));
    }

    @Override
    public LocalDateTime getResult(ResultSet rs, String columnName) throws SQLException {
        final String string = rs.getString(columnName);
        return Optional.ofNullable(string).map(i -> LocalDateTime.parse(i, FORMATTER)).orElse(null);
    }

    @Override
    public LocalDateTime getResult(ResultSet rs, int columnIndex) throws SQLException {
        final String string = rs.getString(columnIndex);
        return Optional.ofNullable(string).map(i -> LocalDateTime.parse(i, FORMATTER)).orElse(null);
    }

    @Override
    public LocalDateTime getResult(CallableStatement cs, int columnIndex) throws SQLException {
        final String string = cs.getString(columnIndex);
        return Optional.ofNullable(string).map(i -> LocalDateTime.parse(i, FORMATTER)).orElse(null);
    }
}
