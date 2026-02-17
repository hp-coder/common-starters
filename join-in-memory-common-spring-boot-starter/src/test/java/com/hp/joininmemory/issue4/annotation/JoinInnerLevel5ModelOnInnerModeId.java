package com.hp.joininmemory.issue4.annotation;

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
        keyFromJoinData = "innerId",
        loader = "@joinInnerRepository.findAllLevel5ByInnerId(#root)"
)
public @interface JoinInnerLevel5ModelOnInnerModeId {

    @AliasFor(annotation = JoinInMemory.class, value="keyFromSourceData")
    @Language("SpEL")
    String keyFromSourceData();

    @AliasFor(annotation = JoinInMemory.class, value="runLevel")
    ExecuteLevel runLevel() default ExecuteLevel.FIFTH;
}
