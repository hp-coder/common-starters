// --- Auto Generated By CodeGen Module ---
package com.luban.codegen.test.api.order.response;

import com.luban.common.base.annotations.FieldDesc;
import com.luban.common.base.model.Response;
import java.lang.Integer;
import java.lang.String;
import lombok.Data;

/**
 * @author hp
 */
@Data
public class TestOrderPageResponse implements Response {
    private String id;

    private String createdAt;

    private String updatedAt;

    @FieldDesc("状态")
    private Integer status;
}
