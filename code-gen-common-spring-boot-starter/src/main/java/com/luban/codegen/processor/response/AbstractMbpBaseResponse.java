package com.luban.codegen.processor.response;

import lombok.Getter;
import lombok.Setter;

/**
 * @author hp
 */
@Getter
@Setter
public abstract class AbstractMbpBaseResponse {
    private String id;
    private String createdAt;
    private String updatedAt;
}
