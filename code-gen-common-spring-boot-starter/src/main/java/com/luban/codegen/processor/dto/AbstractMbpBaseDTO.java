package com.luban.codegen.processor.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * @author programmer
 */
@Getter
@Setter
public abstract class AbstractMbpBaseDTO {

    private Long id;

    private Instant createdAt;

    private Instant updatedAt;

    private Integer version;
}
