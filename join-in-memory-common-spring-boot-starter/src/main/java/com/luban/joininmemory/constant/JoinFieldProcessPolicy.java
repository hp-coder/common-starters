package com.luban.joininmemory.constant;

import java.util.Objects;

/**
 * @author hp
 */
public enum JoinFieldProcessPolicy {
    GROUPED,
    SEPERATED,
    ;

    public boolean isGrouped(){
        return Objects.equals(GROUPED, this);
    }
}
