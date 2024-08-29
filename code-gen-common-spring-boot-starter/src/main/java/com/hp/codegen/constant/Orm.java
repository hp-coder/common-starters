package com.hp.codegen.constant;

import com.google.common.collect.Lists;
import com.hp.codegen.modifier.FieldTypeConverter;
import com.hp.codegen.modifier.converter.jpa.JpaConverterFieldTypeConverter;
import com.hp.codegen.modifier.converter.mybatis.MybatisTypeHandlerFieldTypeConverter;
import com.hp.common.base.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;
import java.util.Optional;

/**
 * @author hp
 */
@Getter
@AllArgsConstructor
public enum Orm implements BaseEnum<Orm, String> {
    /***/
    SPRING_DATA_JPA("jpa", "Spring Data JPA") {
        @Override
        public Collection<FieldTypeConverter> getFieldTypeConverter() {
            return Lists.newArrayList(new JpaConverterFieldTypeConverter());
        }
    },
    MYBATIS_PLUS("mybatis", "Mybatis-Plus") {
        @Override
        public Collection<FieldTypeConverter> getFieldTypeConverter() {
            return Lists.newArrayList(new MybatisTypeHandlerFieldTypeConverter());
        }
    },
    ;
    private final String code;
    private final String name;

    public static Optional<Orm> of(String code) {
        return Optional.ofNullable(BaseEnum.parseByCode(Orm.class, code));
    }


    public Collection<FieldTypeConverter> getFieldTypeConverter() {
        throw new IllegalStateException("No implementation found on orm instance");
    }
}
