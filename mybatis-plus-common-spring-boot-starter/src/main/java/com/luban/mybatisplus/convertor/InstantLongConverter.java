package com.luban.mybatisplus.convertor;

import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

public class InstantLongConverter implements TypeHandlerCodeGenAdapter<Instant, Long> {

    @Override
    public void setParameter(PreparedStatement ps, int i, Instant instant, JdbcType jdbcType) throws SQLException {
        ps.setLong(i, instant.toEpochMilli());
    }

    @Override
    public Instant getResult(ResultSet rs, String columnName) throws SQLException {
        return Instant.ofEpochMilli(rs.getLong(columnName));
    }

    @Override
    public Instant getResult(ResultSet rs, int columnIndex) throws SQLException {
        return Instant.ofEpochMilli(rs.getLong(columnIndex));
    }

    @Override
    public Instant getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return Instant.ofEpochMilli(cs.getLong(columnIndex));
    }
}

