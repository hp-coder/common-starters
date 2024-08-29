package com.hp.id;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author hp
 */
@Data
@ConfigurationProperties(prefix = "id")
public class IdProperties {

    private Long workerId = 1L;

    private Long dataCenterId = 1L;
}
