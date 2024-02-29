package com.hp.jpa.exception;

import lombok.Getter;

/**
 * @author hp
 * @date 2022/10/18
 */
@Getter
public class ValidateResult {
    private final String name;
    private final String message;

    public ValidateResult(String name, String message) {
        this.name = name;
        this.message = message;
    }
}
