package com.hp.excel.util;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

/**
 * @author HP
 * @date 2022/11/8
 */
public final class ValidateUtil {
    private static final Validator VALIDATOR;

    public static <T> Set<ConstraintViolation<T>> validate(T object) {
        return VALIDATOR.validate(object, new Class[0]);
    }

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        VALIDATOR = factory.getValidator();
    }
}
