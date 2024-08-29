package com.hp.mybatisplus.converter;


import cn.hutool.core.util.StrUtil;
import com.hp.common.base.enums.BaseEnum;
import com.hp.mybatisplus.annotation.Converter;
import jakarta.annotation.Nonnull;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * @author hp
 */
@Converter
public abstract class IntegerBasedBaseEnumTypesConverter<T extends Enum<T> & BaseEnum<T, Integer>, FIELD extends Collection<T>> extends AbstractBaseEnumTypesConverter<T, Integer, FIELD> {

    public IntegerBasedBaseEnumTypesConverter() {
        super(StrUtil.COMMA);
    }

    public IntegerBasedBaseEnumTypesConverter(String separator) {
        super(separator);
    }

    @Override
    protected Integer convert(@Nonnull String code) {
        return Integer.parseInt(code);
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, Collection<T> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, fieldToColumn(parameter));
    }

    @Override
    public Collection<T> getResult(ResultSet rs, String columnName) throws SQLException {
        return columnToField(rs.getString(columnName));
    }

    @Override
    public Collection<T> getResult(ResultSet rs, int columnIndex) throws SQLException {
        return columnToField(rs.getString(columnIndex));
    }

    @Override
    public Collection<T> getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return columnToField(cs.getString(columnIndex));
    }

}
