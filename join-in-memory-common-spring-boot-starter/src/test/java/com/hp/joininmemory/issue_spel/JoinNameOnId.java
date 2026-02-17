package com.hp.joininmemory.issue_spel;

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
        loader = "@userRepositoryAdapter.findAllByIdIn(#this)",
        joinDataConverter = "#this.name"
)
public @interface JoinNameOnId {

    @AliasFor(annotation = JoinInMemory.class, value="keyFromSourceData")
    @Language("SpEL")
    String value();

    @AliasFor(annotation = JoinInMemory.class, value="runLevel")
    ExecuteLevel runLevel() default ExecuteLevel.FIFTH;
}
