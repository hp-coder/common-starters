package com.hp.id;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author hp
 */
@RequiredArgsConstructor
public class TweeterSnowflakeBasedIdGenerator implements IdGenerator, Serializable {
    @Serial
    private static final long serialVersionUID = 631698590086075365L;

    private final long workerId;
    private final long dataCenterId;

    @Override
    public long nextId() {
        return snowflakeId(workerId, dataCenterId);
    }

    public synchronized long snowflakeId(long workerId, long datacenterId) {
        Snowflake snowflake = IdUtil.getSnowflake(workerId, datacenterId);
        return snowflake.nextId();
    }

}
