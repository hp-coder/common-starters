package com.hp.joininmemory.constant;

import java.util.Objects;

/**
 * @author hp
 */
public enum JoinFieldProcessPolicy {
    GROUPED,
    SEPARATED,
    ;

    public boolean isGrouped(){
        return Objects.equals(GROUPED, this);
    }
}
