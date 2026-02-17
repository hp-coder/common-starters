package com.hp.joininmemory.feature_filter;

import com.hp.joininmemory.annotation.JoinInMemory;
import com.hp.joininmemory.constant.ExecuteLevel;
import org.intellij.lang.annotations.Language;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author hp
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@JoinInMemory(
        keyFromJoinData = "id",
        loader = "@joinRepository.findAllById(#root)",
        joinDataConverter = "#root.name"
)
public @interface JoinFilterDataUsernameOnUserId {

    @AliasFor(annotation = JoinInMemory.class, value="sourceDataFilter")
    @Language("SpEL")
    String sourceDataFilter() default "";

    @AliasFor(annotation = JoinInMemory.class, value="keyFromSourceData")
    @Language("SpEL")
    String value();

    @AliasFor(annotation = JoinInMemory.class, value="joinDataFilter")
    @Language("SpEL")
    String joinDataFilter() default "";

    @AliasFor(annotation = JoinInMemory.class, value="runLevel")
    ExecuteLevel runLevel() default ExecuteLevel.FIFTH;
}
