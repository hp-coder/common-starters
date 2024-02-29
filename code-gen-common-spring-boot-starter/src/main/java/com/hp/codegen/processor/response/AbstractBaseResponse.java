package com.hp.codegen.processor.response;

import com.hp.common.base.model.Response;
import lombok.Data;

/**
 * @author hp
 */
@Deprecated
@Data
public abstract class AbstractBaseResponse implements Response {
    private String id;
    private String createdAt;
    private String updatedAt;
}
