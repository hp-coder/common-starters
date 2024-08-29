package com.hp.jpa.converter;

import cn.hutool.core.math.Money;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Optional;

/**
 * @author hp
 */
@Converter
public abstract class AbstractMoneyConverter implements AttributeConverter<Money, Long> {

    /**
     * getCent指的是该种类货币的最小单位, 例如人民币是分, 日元是元
     */
    @Override
    public Long convertToDatabaseColumn(Money money) {
        return Optional.ofNullable(money).map(Money::getCent).orElse(null);
    }

}
