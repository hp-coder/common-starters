package com.hp.jpa.converter;

import com.hp.common.base.enums.ValidStatus;
import jakarta.persistence.Converter;

/**
 * @author hp 2023/4/10
 */
@Converter
public class ValidStatusConverter extends IntegerBasedBaseEnumTypeConverter<ValidStatus> {
}
