package com.luban.codegen.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author hp
 */
@Getter
@AllArgsConstructor
public enum Orm {
    /***/
    SPRING_DATA_JPA("jpa", "Spring Data JPA"),
    MYBATIS_PLUS("mbp", "Mybatis-Plus"),
    ;

    private final String code;
    private final String name;

    public static Optional<Orm> of(String code) {
        return Arrays.stream(Orm.values())
                        .filter(i -> Objects.equals(i.getCode(), code))
                        .findFirst();
    }
}
