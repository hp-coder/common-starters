package com.luban.mybatisplus;

import com.baomidou.mybatisplus.annotation.*;
import com.luban.mybatisplus.convertor.InstantLongConverter;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.time.Instant;

@Data
public abstract class BaseMbpAggregate {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField(value = "created_at",
            updateStrategy = FieldStrategy.IGNORED,
            typeHandler = InstantLongConverter.class
    )
    @Setter(AccessLevel.PROTECTED)
    private Instant createdAt;

    @TableField(value = "updated_at",
            typeHandler = InstantLongConverter.class
    )
    @Setter(AccessLevel.PROTECTED)
    private Instant updatedAt;

    @Version
    @TableField(value = "version")
    @Setter(AccessLevel.PRIVATE)
    private Integer version;

}
