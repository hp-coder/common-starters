
package com.hp.joininmemory.exception;

import java.util.function.BiConsumer;

/**
 * @author hp
 */
public interface ExceptionNotifier {

    BiConsumer<Object,Throwable> handle();
}
