package com.hp.mybatisplus;

import com.baomidou.mybatisplus.annotation.*;
import com.hp.mybatisplus.convertor.LocalDateTimeTypeConverter;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
public abstract class BaseMbpAggregate {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField(
            value = "created_at",
            updateStrategy = FieldStrategy.IGNORED,
            update = "now()",
            typeHandler = LocalDateTimeTypeConverter.class
    )
    @Setter(AccessLevel.PROTECTED)
    private LocalDateTime createdAt;

    @TableField(
            value = "updated_at",
            update = "now()",
            typeHandler = LocalDateTimeTypeConverter.class
    )
    @Setter(AccessLevel.PROTECTED)
    private LocalDateTime updatedAt;

    @Version
    @TableField(value = "version")
    @Setter(AccessLevel.PRIVATE)
    private Integer version;

}
