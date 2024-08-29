package com.hp.mybatisplus.converter;

import com.hp.mybatisplus.annotation.Converter;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Optional;

@Converter
public class InstantLongConverter implements TypeHandlerAdapter<Instant, Long> {

    @Override
    public void setParameter(PreparedStatement ps, int i, Instant instant, JdbcType jdbcType) throws SQLException {
        ps.setLong(i, fieldToColumn(instant));
    }

    @Override
    public Instant getResult(ResultSet rs, String columnName) throws SQLException {
        return Instant.ofEpochMilli(rs.getLong(columnName));
    }

    @Override
    public Instant getResult(ResultSet rs, int columnIndex) throws SQLException {
        return columnToField(rs.getLong(columnIndex));
    }

    @Override
    public Instant getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return columnToField(cs.getLong(columnIndex));
    }

    @Override
    public Long fieldToColumn(Instant instant) {
        return Optional.ofNullable(instant).map(Instant::toEpochMilli).orElse(null);
    }

    @Override
    public Instant columnToField(Long epochMilli) {
        return Optional.ofNullable(epochMilli).map(Instant::ofEpochMilli).orElse(null);
    }
}

