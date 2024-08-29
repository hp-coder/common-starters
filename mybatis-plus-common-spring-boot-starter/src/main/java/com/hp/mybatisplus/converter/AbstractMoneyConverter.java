package com.hp.mybatisplus.converter;

import cn.hutool.core.math.Money;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * @author hp
 */
public abstract class AbstractMoneyConverter implements TypeHandlerAdapter<Money, Long> {

    /**
     * getCent指的是该种类货币的最小单位, 例如人民币是分, 日元是元
     */
    @Override
    public Long fieldToColumn(Money money) {
        return Optional.ofNullable(money).map(Money::getCent).orElse(null);
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, Money parameter, JdbcType jdbcType) throws SQLException {
        ps.setLong(i, fieldToColumn(parameter));
    }

    @Override
    public Money getResult(ResultSet rs, String columnName) throws SQLException {
        return columnToField(rs.getLong(columnName));
    }

    @Override
    public Money getResult(ResultSet rs, int columnIndex) throws SQLException {
        return columnToField(rs.getLong(columnIndex));
    }

    @Override
    public Money getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return columnToField(cs.getLong(columnIndex));
    }
}
