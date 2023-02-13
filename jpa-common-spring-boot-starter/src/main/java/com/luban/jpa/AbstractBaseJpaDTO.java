package com.luban.jpa;

import lombok.Data;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Data
public class AbstractBaseJpaDTO implements Serializable {
    private static final long serialVersionUID = 8133324612576327441L;

    private int version;

    private Long id;

    private String createdAt;

    private String updatedAt;

    protected AbstractBaseJpaDTO(){}

    protected AbstractBaseJpaDTO(BaseJpaAggregate source) {
        this.setVersion(source.getVersion());
        this.setId(source.getId());
        this.setCreatedAt(source.getCreatedAt().atZone(ZoneId.of("Asia/Shanghai")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        this.setUpdatedAt(source.getUpdatedAt().atZone(ZoneId.of("Asia/Shanghai")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }

}
