package com.hp.joininmemory.feature_dynamic_fields;

import com.hp.joininmemory.annotation.JoinInMemory;
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
        keyFromJoinData = "userId",
        loader = "@userRepository.findByUserId(#root)",
        joinDataConverter = "#this.username"
)
public @interface JoinUsernameOnUserId {

    @AliasFor(annotation = JoinInMemory.class, value = "keyFromSourceData")
    @Language("SpEL")
    String value();
}
