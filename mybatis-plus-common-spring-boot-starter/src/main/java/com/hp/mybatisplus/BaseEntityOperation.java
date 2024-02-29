package com.hp.mybatisplus;

import com.hp.mybatisplus.exception.ValidateResult;
import com.hp.mybatisplus.exception.ValidationException;
import com.hp.mybatisplus.group.ValidateGroup;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author hp
 * @date 2022/10/18
 */
public abstract class BaseEntityOperation implements EntityOperation {

    static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    public <T> void doValidate(T t, Class<? extends ValidateGroup> group) {
        final Set<ConstraintViolation<T>> constraintViolations = VALIDATOR.validate(t, group, Default.class);
        if (!CollectionUtils.isEmpty(constraintViolations)) {
            final List<ValidateResult> validateResults = constraintViolations.stream()
                    .map(_0 -> new ValidateResult(_0.getPropertyPath().toString(), _0.getMessage()))
                    .collect(Collectors.toList());
            throw new ValidationException(validateResults);
        }

    }
}
