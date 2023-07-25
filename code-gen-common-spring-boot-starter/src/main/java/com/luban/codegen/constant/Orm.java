package com.luban.codegen.constant;

import com.luban.common.base.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

/**
 * @author hp
 */
@Getter
@AllArgsConstructor
public enum Orm implements BaseEnum<Orm, String> {
    /***/
    SPRING_DATA_JPA("jpa", "Spring Data JPA"),
    MYBATIS_PLUS("mbp", "Mybatis-Plus"),
    ;

    private final String code;
    private final String name;

    public static Optional<Orm> of(String code) {
        return Optional.ofNullable(BaseEnum.parseByCode(Orm.class, code));
    }
}
