package com.luban.codegen.processor.dto.jpa;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * @author programmer
 */
@Getter
@Setter
public abstract class AbstractBaseDTO {

    private Long id;

    private Instant createdAt;

    private Instant updatedAt;

    private Integer version;
}
