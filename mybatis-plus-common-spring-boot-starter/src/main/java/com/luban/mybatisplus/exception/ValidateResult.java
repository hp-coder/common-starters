package com.luban.mybatisplus.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hp
 * @date 2022/10/18
 */
@Getter
@AllArgsConstructor
public class ValidateResult {

    private final String name;
    private final String message;
}
