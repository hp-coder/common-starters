package com.hp.mybatisplus.converter;

import com.hp.common.base.valueobject.AbstractSingleValueObject;
import com.hp.common.base.valueobject.AbstractStringBasedSingleValueObject;
import com.hp.mybatisplus.annotation.Converter;
import jakarta.annotation.Nonnull;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;


@Converter
public abstract class AbstractStringBasedSingleValueObjectConverter<T extends AbstractStringBasedSingleValueObject> implements TypeHandlerAdapter<T, String> {

    @Override
    public void setParameter(PreparedStatement ps, int i, T t, JdbcType jdbcType) throws SQLException {
        ps.setString(i, fieldToColumn(t));
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

    @Override
    public String fieldToColumn(T source) {
        return Optional.ofNullable(source).map(AbstractSingleValueObject::value).orElse(null);
    }

    @Override
    public T columnToField(String target) {
        return Optional.ofNullable(target).map(this::valueObject).orElse(null);
    }

    protected abstract T valueObject(@Nonnull String value);
}
