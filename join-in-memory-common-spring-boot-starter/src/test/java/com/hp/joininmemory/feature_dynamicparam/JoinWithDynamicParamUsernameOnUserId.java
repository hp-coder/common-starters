package com.hp.joininmemory.feature_dynamicparam;

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
        loader = "@joinRepository.findAllById(#root, #param1)",
        joinDataConverter = "#root.name"
)
public @interface JoinWithDynamicParamUsernameOnUserId {

    @AliasFor(annotation = JoinInMemory.class, value = "keyFromSourceData")
    @Language("SpEL")
    String value();

    @AliasFor(annotation = JoinInMemory.class, value = "runLevel")
    ExecuteLevel runLevel() default ExecuteLevel.FIFTH;

    @Language("SpEL")
    String param1() default "";
}
