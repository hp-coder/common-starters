package com.hp.biz.logger.exception;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface BizLoggerExceptionNotifier {

    BiConsumer<Object,Throwable> handle();
}
