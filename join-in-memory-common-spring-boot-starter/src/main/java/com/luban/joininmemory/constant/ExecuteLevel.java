
package com.luban.joininmemory.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The smaller the value, the higher the priority.
 *
 * @author hp
 */
@Getter
@AllArgsConstructor
public enum ExecuteLevel {
    /***/
    FIRST(1),
    SECOND(2),
    THIRD(3),
    FOURTH(4),
    FIFTH(5),
    SIXTH(6),
    SEVENTH(7),
    EIGHTH(8),
    NINTH(9),
    TENTH(10),
    ;
    private final Integer code;
}
