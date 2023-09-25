package com.luban.codegen.processor.response;

import com.luban.common.base.model.Response;
import lombok.Data;

/**
 * @author hp
 */
@Data
public abstract class AbstractBaseResponse implements Response {
    private String id;
    private String createdAt;
    private String updatedAt;
}
