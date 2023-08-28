package com.luban.codegen.processor.response.jpa;

import lombok.Getter;
import lombok.Setter;

/**
 * @author hp
 */
@Getter
@Setter
public abstract class AbstractBaseResponse {
    private String id;
    private String createdAt;
    private String updatedAt;
}
