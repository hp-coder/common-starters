package com.hp.jpa.converter;


import com.hp.common.base.enums.BaseEnum;
import jakarta.persistence.Converter;

/**
 * @author hp
 */
@Converter
public abstract class StringBasedBaseEnumTypeConverter<T extends Enum<T> & BaseEnum<T, String>> extends AbstractBaseEnumTypeConverter<T, String> {

}
