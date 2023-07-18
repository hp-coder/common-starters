package com.hp.mybatisplus;

import lombok.Data;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Data
public class AbstractBaseMbpDTO implements Serializable {

    private static final long serialVersionUID = 1832817670069337558L;
    private Long id;

    private String createdAt;

    private String updatedAt;

    private int version;

    protected AbstractBaseMbpDTO(){}

    protected AbstractBaseMbpDTO(BaseMbpAggregate source) {
        this.setId(source.getId());
        this.setCreatedAt(source.getCreatedAt().atZone(ZoneId.of("Asia/Shanghai")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        this.setUpdatedAt(source.getUpdatedAt().atZone(ZoneId.of("Asia/Shanghai")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        this.setVersion(source.getVersion());
    }

}
