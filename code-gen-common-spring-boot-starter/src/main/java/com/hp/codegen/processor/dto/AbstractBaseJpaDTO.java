package com.hp.codegen.processor.dto;

import com.hp.jpa.BaseJpaAggregate;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Data
public class AbstractBaseJpaDTO implements Serializable {
    private static final long serialVersionUID = 8133324612576327441L;
    @Schema(
            title = "数据版本"
    )
    private int version;
    @Schema(
            title = "主键"
    )
    private Long id;
    @Schema(
            title = "创建时间"
    )
    private String createdAt;
    @Schema(
            title = "修改时间"
    )
    private String updatedAt;

    protected AbstractBaseJpaDTO(){}

    protected AbstractBaseJpaDTO(BaseJpaAggregate source) {
        this.setVersion(source.getVersion());
        this.setId(source.getId());
        this.setCreatedAt(source.getCreatedAt().atZone(ZoneId.of("Asia/Shanghai")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        this.setUpdatedAt(source.getUpdatedAt().atZone(ZoneId.of("Asia/Shanghai")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }

}