package com.hp.mybatisplus.converter;

import com.hp.common.base.valueobject.socialcreditcode.SocialCreditCode;
import com.hp.mybatisplus.annotation.Converter;
import jakarta.annotation.Nonnull;

/**
 * @author hp
 */
@Converter
public class SocialCreditCodeTypeConverter extends AbstractStringBasedSingleValueObjectConverter<SocialCreditCode> {

    @Override
    protected SocialCreditCode valueObject(@Nonnull String value) {
        return SocialCreditCode.of(value);
    }
}
