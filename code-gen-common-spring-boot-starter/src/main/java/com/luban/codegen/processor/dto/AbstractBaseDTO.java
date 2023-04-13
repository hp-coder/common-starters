package com.luban.codegen.processor.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public abstract class AbstractBaseDTO {

    private Long id;

    private Instant createdAt;

    private Instant updatedAt;

    private Integer version;
}
