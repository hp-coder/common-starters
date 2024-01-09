package com.hp.joininmemory.example;

import com.hp.joininmemory.annotation.JoinInMemory;
import com.hp.joininmemory.constant.ExecuteLevel;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author hp
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@JoinInMemory(
        keyFromSourceData = "",
        sourceDataKeyConverter = "#{ #root != null ? T(java.lang.Long).parseLong(#root) : null}",
        keyFromJoinData = "#{id}",
        joinDataKeyConverter = "",
        loader = "#{@joinRepository.findAllById(#root)}",
        joinDataConverter = "#{#root.name}"
)
public @interface JoinUsernameOnUserId {

    @AliasFor(annotation = JoinInMemory.class, value="keyFromSourceData")
    String keyFromSourceData();

    @AliasFor(annotation = JoinInMemory.class, value="sourceDataKeyConverter")
    String sourceDataKeyConverter() default "#{ #root != null ? T(java.lang.Long).parseLong(#root) : null}";

    @AliasFor(annotation = JoinInMemory.class, value = "runLevel")
    ExecuteLevel runLevel() default ExecuteLevel.FIFTH;
}
