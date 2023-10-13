package com.luban.codegen.processor.dto.mybatisplus;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author programmer
 */
@Getter
@Setter
public abstract class AbstractMbpBaseDTO {

    private Long id;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Integer version;
}
