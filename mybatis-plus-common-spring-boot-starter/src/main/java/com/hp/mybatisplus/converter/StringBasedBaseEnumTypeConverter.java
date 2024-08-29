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
public abstract class StringBasedBaseEnumTypeConverter<T extends Enum<T> & BaseEnum<T, String>> extends AbstractBaseEnumTypeConverter<T, String> {

    @Override
    public void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, fieldToColumn(parameter));
    }

    @Override
    public T getResult(ResultSet rs, String columnName) throws SQLException {
        return columnToField(rs.getString(columnName));
    }

    @Override
    public T getResult(ResultSet rs, int columnIndex) throws SQLException {
        return columnToField(rs.getString(columnIndex));
    }

    @Override
    public T getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return columnToField(cs.getString(columnIndex));
    }
}
