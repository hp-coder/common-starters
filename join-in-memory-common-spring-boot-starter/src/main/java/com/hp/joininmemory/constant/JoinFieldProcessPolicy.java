package com.hp.joininmemory.constant;

import java.util.Objects;

/**
 * For {@link JoinFieldProcessPolicy#GROUPED}, the framework will try to group the join fields using the same join
 * annotation into one executor.
 * <p>
 * For {@link JoinFieldProcessPolicy#SEPARATED}, the framework will treat each join field as a separate executor.
 *
 * @author <a href="mailto:max_verstrappon@outlook.com">HuPeng</a>
 */
public enum JoinFieldProcessPolicy {
    GROUPED,
    SEPARATED,
    ;

    public boolean isGrouped() {
        return Objects.equals(GROUPED, this);
    }
}
