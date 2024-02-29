package com.hp.extension.executor;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author hp
 * @date 2022/10/19
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Component
public @interface Extension {
    String bizId();
}
