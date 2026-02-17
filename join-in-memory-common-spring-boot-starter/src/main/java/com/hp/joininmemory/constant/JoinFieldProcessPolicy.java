package com.hp.joininmemory.constant;

import java.util.Objects;

/**
 * The JoinFieldProcessPolicy enum defines strategies for processing join fields.
 *
 * <p>For the {@link JoinFieldProcessPolicy#GROUPED} strategy,
 * the framework attempts to group join fields with the same join annotation
 * into a single executor. This approach significantly reduces I/O operations
 * and is recommended for performance optimization.</p>
 *
 * <p>For the {@link JoinFieldProcessPolicy#SEPARATED} strategy,
 * the framework treats each join field as an independent executor.</p>
 *
 * @version 1.0.0
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026/1/6
 */
public enum JoinFieldProcessPolicy {
    GROUPED,
    SEPARATED,
    ;

    public boolean isGrouped() {
        return Objects.equals(GROUPED, this);
    }
}
