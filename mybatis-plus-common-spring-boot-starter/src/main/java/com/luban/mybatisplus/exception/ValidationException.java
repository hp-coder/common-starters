package com.hp.mybatisplus.exception;

import lombok.Getter;

import java.util.List;

/**
 * @author HP
 * @date 2022/10/18
 */
@Getter
public class ValidationException extends RuntimeException {
    private final List<ValidateResult> result;

    public ValidationException(List<ValidateResult> result) {
        this.result = result;
    }

}
