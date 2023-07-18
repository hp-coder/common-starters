package com.hp.mybatisplus;

import lombok.Data;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Data
public class AbstractBaseMbpVO implements Serializable {

    private static final long serialVersionUID = 1912374480677443812L;
    private int version;

    private String id;

    private String createdAt;

    private String updatedAt;

    protected AbstractBaseMbpVO(){}

    protected AbstractBaseMbpVO(BaseMbpAggregate source) {
        this.setVersion(source.getVersion());
        this.setId(String.valueOf(source.getId()));
        this.setCreatedAt(source.getCreatedAt().atZone(ZoneId.of("Asia/Shanghai")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        this.setUpdatedAt(source.getUpdatedAt().atZone(ZoneId.of("Asia/Shanghai")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }

}
