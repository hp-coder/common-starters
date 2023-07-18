package com.hp.codegen.processor.vo;

import com.hp.jpa.BaseJpaAggregate;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public abstract class AbstractBaseVO {

    private Long id;

    private Instant createdAt;

    private Instant updatedAt;

    private Integer version;

    protected AbstractBaseVO() {
    }

    protected AbstractBaseVO(BaseJpaAggregate source) {
        this.setId(source.getId());
        this.setCreatedAt(source.getCreatedAt());
        this.setUpdatedAt(source.getUpdatedAt());
        this.setVersion(source.getVersion());
    }
}
