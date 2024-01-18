package com.hp.joininmemory.example;

import com.hp.joininmemory.exception.JoinExceptionNotifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

/**
 * @author hp
 */
@Component
@Slf4j
public class DefaultJoinExceptionNotifier implements JoinExceptionNotifier {
    @Override
    public BiConsumer<Object, Throwable> handle() {
        return (data, ex) -> {
            log.error("join异常", ex);
        };
    }
}
