package com.hp.joininmemory.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The lower the value, the higher the priority.
 * <p>
 * When a nested join is present, the actual run level will be recalculated according to the number of nested layers.
 * <p>
 * Generally, the first layer uses its own run level, while consecutive layers use the first layer's run level plus
 * the number of layers.
 *
 * @version 1.0.0
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026/1/6
 */
@Getter
@AllArgsConstructor
public enum ExecuteLevel {
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
