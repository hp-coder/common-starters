package com.hp.codegen.processor.vo;

import com.hp.jpa.BaseJpaAggregate;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Data
public class AbstractBaseJpaVO implements Serializable {
    private static final long serialVersionUID = 2078591511770817021L;
    @Schema(
            title = "数据版本"
    )
    private int version;
    @Schema(
            title = "主键"
    )
    private String id;
    @Schema(
            title = "创建时间"
    )
    private String createdAt;
    @Schema(
            title = "修改时间"
    )
    private String updatedAt;

    protected AbstractBaseJpaVO() {
    }

    protected AbstractBaseJpaVO(BaseJpaAggregate source) {
        this.setVersion(source.getVersion());
        this.setId(String.valueOf(source.getId()));
        this.setCreatedAt(source.getCreatedAt().atZone(ZoneId.of("Asia/Shanghai")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        this.setUpdatedAt(source.getUpdatedAt().atZone(ZoneId.of("Asia/Shanghai")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }
}