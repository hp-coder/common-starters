package com.hp.jpa.converter;


import com.hp.common.base.enums.BaseEnum;
import jakarta.persistence.Converter;

/**
 * @author hp
 */
@Converter
public abstract class IntegerBasedBaseEnumTypeConverter<T extends Enum<T> & BaseEnum<T, Integer>> extends AbstractBaseEnumTypeConverter<T, Integer> {

}
