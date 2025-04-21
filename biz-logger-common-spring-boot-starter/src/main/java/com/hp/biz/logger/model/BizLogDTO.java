package com.hp.biz.logger.model;

import com.hp.common.base.annotation.FieldDesc;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Collection;

/**
 * @author hp
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BizLogDTO {

    @FieldDesc("@EnableBizLogger上的租户信息")
    private String tenant;

    @FieldDesc("标题")
    private String title;

    @FieldDesc("业务唯一编码")
    private String bizNo;
    @FieldDesc("类型")
    private String type;
    @FieldDesc("子类型")
    private String subType;
    @FieldDesc("区分可见范围")
    private String scope;

    @FieldDesc("日志信息")
    private String action;

    @FieldDesc("操作人")
    private BizLogOperator operator;
    @FieldDesc("操作时间")
    private Instant operatedAt;

    @FieldDesc("是否成功")
    private boolean succeed;

    @FieldDesc("差异日志")
    private Collection<BizDiffDTO> diffs;

    @FieldDesc("调用信息")
    private InvocationInfo invocationInfo;

    public void succeed(String action) {
        this.succeed = true;
        this.action = action;
    }

    public void failed(String action) {
        this.succeed = false;
        this.action = action;
    }
}
