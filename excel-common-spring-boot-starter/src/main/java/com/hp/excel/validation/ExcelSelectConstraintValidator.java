package com.hp.excel.validation;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.hp.common.base.utils.SpELHelper;
import com.hp.excel.annotation.ExcelOptions;
import com.hp.excel.annotation.ExcelSelect;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * @author hp
 * @date 2022/11/7
 */
@Slf4j
public class ExcelSelectConstraintValidator implements ConstraintValidator<ExcelSelect, String> {

    private ExcelSelect excelSelect = null;
    private Map<Object, Collection<Object>> cascadeOptions = null;
    private Collection<Object> options = null;

    @Override
    public void initialize(ExcelSelect excelSelect) {
        this.excelSelect = excelSelect;
        if (StrUtil.isNotEmpty(excelSelect.parentColumnName())) {
            cascadeOptions = resolveOptions(excelSelect);
        } else {
            options = resolveOptions(excelSelect);
        }
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (StrUtil.isEmpty(value)) {
            return true;
        }
        final ConstraintValidatorContextImpl unwrap = constraintValidatorContext.unwrap(ConstraintValidatorContextImpl.class);

        if (StrUtil.isNotEmpty(this.excelSelect.parentColumnName())) {
            return Optional.ofNullable(cascadeOptions.get("")).map(opts -> opts.stream().map(String::valueOf).toList().contains(value)).orElse(true);
        } else {
            return Optional.ofNullable(options).map(opts -> opts.stream().map(String::valueOf).toList().contains(value)).orElse(true);
        }
    }

    @SuppressWarnings("unchecked")
    @Nullable
    protected <T> T resolveOptions(@Nonnull ExcelSelect excelSelect) {
        final ExcelOptions excelOptions = excelSelect.options();
        if (StrUtil.isEmpty(excelOptions.expression())) {
            return null;
        }
        final SpELHelper spELHelper = SpringUtil.getBean(SpELHelper.class);
        // TODO 暂不支持校验时的条件
        return (T) spELHelper.newGetterInstance(excelOptions.expression()).apply(
                null,
                evaluationContext -> {
                }
        );
    }
}
