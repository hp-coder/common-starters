package com.luban.codegen.processor.request.jpa;

import lombok.Getter;
import lombok.Setter;

/**
 * @author hp
 */
@Getter
@Setter
public abstract class AbstractBaseRequest {
    private Long id;
}