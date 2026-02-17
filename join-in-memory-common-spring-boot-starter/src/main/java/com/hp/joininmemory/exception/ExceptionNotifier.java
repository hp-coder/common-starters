package com.hp.joininmemory.exception;

import java.util.function.BiConsumer;

/**
 *
 * @version 1.0.0
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026/1/6
 */
@FunctionalInterface
public interface ExceptionNotifier {

    BiConsumer<Object, Throwable> handle();
}
