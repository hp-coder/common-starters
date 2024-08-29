package com.hp.jpa.converter;

import com.hp.common.base.valueobject.socialcreditcode.SocialCreditCode;
import jakarta.persistence.Converter;

import java.util.Optional;

/**
 * @author hp
 */
@Converter
public class SocialCreditCodeTypeConverter extends AbstractStringBasedSingleValueObjectConverter<SocialCreditCode> {
    @Override
    public SocialCreditCode convertToEntityAttribute(String dbData) {
        return Optional.ofNullable(dbData).map(SocialCreditCode::of).orElse(null);
    }
}
