package com.hp.joininmemory.context;

import com.hp.joininmemory.annotation.JoinInMemoryConfig;
import lombok.Getter;
import lombok.Setter;

/**
 * @author hp
 */
@Getter
@Setter
public class JoinContext<DATA> {

    Class<DATA> dataClass;

    JoinInMemoryConfig config;

    public JoinContext(Class<DATA> dataClass, JoinInMemoryConfig config) {
        this.dataClass = dataClass;
        this.config = config;
    }
}
