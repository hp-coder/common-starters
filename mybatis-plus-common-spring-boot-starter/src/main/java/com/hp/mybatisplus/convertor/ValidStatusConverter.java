package com.hp.mybatisplus.convertor;

import com.hp.common.base.enums.ValidStatus;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author hp
 */
public class ValidStatusConverter implements TypeHandlerCodeGenAdapter<ValidStatus, Integer> {

    @Override
    public void setParameter(PreparedStatement ps, int i, ValidStatus validStatus, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, validStatus.getCode());
    }

    @Override
    public ValidStatus getResult(ResultSet rs, String columnName) throws SQLException {
        final Integer data = rs.getInt(columnName);
        return ValidStatus.of(data).orElse(null);
    }

    @Override
    public ValidStatus getResult(ResultSet rs, int columnIndex) throws SQLException {
        final Integer data = rs.getInt(columnIndex);
        return ValidStatus.of(data).orElse(null);
    }

    @Override
    public ValidStatus getResult(CallableStatement cs, int columnIndex) throws SQLException {
        final Integer data = cs.getInt(columnIndex);
        return ValidStatus.of(data).orElse(null);
    }
}
