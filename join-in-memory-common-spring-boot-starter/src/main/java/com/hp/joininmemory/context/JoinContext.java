package com.hp.joininmemory.context;

import com.hp.joininmemory.annotation.JoinInMemoryConfig;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

/**
 * @author hp
 */
@Getter
@Setter
@JoinInMemoryConfig
public class JoinContext<DATA> {

    Class<DATA> dataClass;

    JoinInMemoryConfig config;

    public JoinContext(Class<DATA> dataClass, JoinInMemoryConfig config) {
        this.dataClass = dataClass;
        this.config = Optional.ofNullable(config).orElse(this.getClass().getAnnotation(JoinInMemoryConfig.class));
    }
}
