package com.hp.excel.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * @author hp
 * @date 2022/11/7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessage {

    private Long lineNum;
    private Set<String> errors = new HashSet<>();

    public ErrorMessage(Set<String> errors) {
        this.errors = errors;
    }

    public ErrorMessage(String error) {
        HashSet<String> objects = new HashSet();
        objects.add(error);
        this.errors = objects;
    }
}
