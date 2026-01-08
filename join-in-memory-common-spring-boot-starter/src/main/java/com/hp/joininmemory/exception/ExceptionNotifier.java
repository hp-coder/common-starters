
package com.hp.joininmemory.exception;

import java.util.function.BiConsumer;

/**
 * @author <a href="mailto:max_verstrappon@outlook.com">HuPeng</a>
 */
@FunctionalInterface
public interface ExceptionNotifier {

    BiConsumer<Object, Throwable> handle();
}
