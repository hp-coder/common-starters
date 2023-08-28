package com.luban.codegen.processor.vo.mybatisplus;

import com.luban.mybatisplus.BaseMbpAggregate;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author programmer
 */
@Getter
@Setter
public abstract class AbstractMbpBaseVO {

    private Long id;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Integer version;

    protected AbstractMbpBaseVO() {
    }

    protected AbstractMbpBaseVO(BaseMbpAggregate source) {
        this.setId(source.getId());
        this.setCreatedAt(source.getCreatedAt());
        this.setUpdatedAt(source.getUpdatedAt());
        this.setVersion(source.getVersion());
    }
}
