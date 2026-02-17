package com.hp.joininmemory.annotation;

import java.lang.annotation.*;

/**
 * A marker to mark fields are nested join fields and tells the framework to check and process nested fields.
 * <p>
 * This feature allows the framework to join nested fields in a single join operation.
 *
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026-2月-10
 * @since 1.1.0
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NestedJoin {
}
