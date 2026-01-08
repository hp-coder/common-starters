package com.hp.joininmemory.context;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.hp.joininmemory.annotation.JoinInMemoryConfig;
import lombok.Getter;

import java.util.Optional;

/**
 * Join entrypoint context
 * <p>
 * if the join class wasn't annotated with {@link JoinInMemoryConfig}, a default config will be used.
 * <p>
 * the default config is set on this context class.
 *
 * @author <a href="mailto:max_verstrappon@outlook.com">HuPeng</a>
 */
@Getter
@JoinInMemoryConfig
public class JoinContext<DATA> {

    private final static TransmittableThreadLocal<JoinContext<?>> JOIN_CONTEXT_THREAD_LOCAL = new TransmittableThreadLocal<>();

    public static void clean() {
        JOIN_CONTEXT_THREAD_LOCAL.remove();
    }

    @SuppressWarnings("unchecked")
    public static <DATA> JoinContext<DATA> get() {
        return (JoinContext<DATA>) JOIN_CONTEXT_THREAD_LOCAL.get();
    }

    /**
     * The join class
     */
    private final Class<DATA> dataClass;

    /**
     * The join config
     * <p>
     * If the join class wasn't annotated with {@link JoinInMemoryConfig}, a default config will be used.
     */
    private final JoinInMemoryConfig config;

    public JoinContext(Class<DATA> dataClass, JoinInMemoryConfig config) {
        this.dataClass = dataClass;
        this.config = Optional.ofNullable(config).orElse(JoinContext.class.getAnnotation(JoinInMemoryConfig.class));
        JOIN_CONTEXT_THREAD_LOCAL.set(this);
    }
}
