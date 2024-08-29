package com.hp.mybatisplus.converter;


import com.hp.common.base.enums.BaseEnum;
import com.hp.mybatisplus.annotation.Converter;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author hp
 */
@Converter
public abstract class IntegerBasedBaseEnumTypeConverter<T extends Enum<T> & BaseEnum<T, Integer>> extends AbstractBaseEnumTypeConverter<T, Integer> {

    @Override
    public void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, fieldToColumn(parameter));
    }

    @Override
    public T getResult(ResultSet rs, String columnName) throws SQLException {
        return columnToField(rs.getInt(columnName));
    }

    @Override
    public T getResult(ResultSet rs, int columnIndex) throws SQLException {
        return columnToField(rs.getInt(columnIndex));
    }

    @Override
    public T getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return columnToField(cs.getInt(columnIndex));
    }
}

