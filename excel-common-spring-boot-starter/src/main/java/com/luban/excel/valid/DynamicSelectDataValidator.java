package com.luban.excel.valid;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.luban.excel.annotation.DynamicSelectData;
import com.luban.excel.handler.ColumnDynamicSelectDataHandler;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Objects;

/**
 * @author hp
 * @date 2022/11/7
 */
@Slf4j
public class DynamicSelectDataValidator implements ConstraintValidator<DynamicSelectData, String> {

    private String arg = null;
    private ColumnDynamicSelectDataHandler handler = null;

    @Override
    public void initialize(DynamicSelectData data) {
        this.arg = data.parameter();
        final Class<? extends ColumnDynamicSelectDataHandler> sourceHandlerClass = data.handler();
        this.handler = SpringUtil.getBean(sourceHandlerClass);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (StrUtil.isEmpty(value) || Objects.isNull(handler)) {
            return true;
        }
        try {
            final List<String> constrainSource = (List<String>) handler.source().apply(arg);
            return constrainSource.contains(value);
        } catch (Exception e) {
            return false;
        }
    }
}
