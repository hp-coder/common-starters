// --- Auto Generated By CodeGen Module ---
package com.luban.codegen.test.api.order.request;

import com.luban.common.base.annotations.FieldDesc;
import com.luban.common.base.model.Request;
import java.lang.Integer;
import lombok.Data;

/**
 * @author hp
 */
@Data
public class TestOrderPageRequest implements Request {
    @FieldDesc("状态")
    private Integer status;
}
