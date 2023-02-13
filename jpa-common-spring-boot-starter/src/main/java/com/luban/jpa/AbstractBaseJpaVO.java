package com.luban.jpa;

import lombok.Data;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Data
public class AbstractBaseJpaVO implements Serializable {
    private static final long serialVersionUID = 2078591511770817021L;

    private int version;

    private String id;

    private String createdAt;

    private String updatedAt;

    protected AbstractBaseJpaVO(){}

    protected AbstractBaseJpaVO(BaseJpaAggregate source) {
        this.setVersion(source.getVersion());
        this.setId(String.valueOf(source.getId()));
        this.setCreatedAt(source.getCreatedAt().atZone(ZoneId.of("Asia/Shanghai")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        this.setUpdatedAt(source.getUpdatedAt().atZone(ZoneId.of("Asia/Shanghai")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }

}
