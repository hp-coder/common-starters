package com.luban.codegen.context;


import com.google.common.collect.Lists;
import com.luban.codegen.constant.Orm;
import com.luban.codegen.processor.modifier.BaseEnumFieldSpecModifier;
import com.luban.codegen.processor.modifier.FieldSpecModifier;
import com.luban.codegen.processor.modifier.LongToStringFieldSpecModifier;
import com.luban.codegen.processor.modifier.jpa.JpaConverterFieldSpecModifier;
import com.luban.codegen.processor.modifier.mybatisplus.MybatisplusTypeHandlerFieldSpecModifier;
import lombok.Getter;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import java.util.Collections;
import java.util.List;

/**
 * @author hp
 * @date 2022/10/24
 */
public final class ProcessingEnvironmentContextHolder {

    private static final ThreadLocal<ProcessingEnvironment> ENVIRONMENT_THREAD_LOCAL = new ThreadLocal<>();

    public static void setEnvironment(ProcessingEnvironment environment) {
        ENVIRONMENT_THREAD_LOCAL.set(environment);
    }

    public static ProcessingEnvironment getEnvironment() {
        return ENVIRONMENT_THREAD_LOCAL.get();
    }

    public static Messager getMessager() {
        return getEnvironment().getMessager();
    }

    @Getter
    private static List<FieldSpecModifier> fieldSpecModifiers = null;

    public static void setOrm(Orm orm) {
        initModifiers(orm);
    }

    private static void initModifiers(Orm orm) {
        if (Orm.SPRING_DATA_JPA.equals(orm)) {
            fieldSpecModifiers = Collections.unmodifiableList(
                    Lists.newArrayList(
                            new LongToStringFieldSpecModifier(),
                            new BaseEnumFieldSpecModifier(),
                            new JpaConverterFieldSpecModifier()
                    ));
        } else if (Orm.MYBATIS_PLUS.equals(orm)) {
            fieldSpecModifiers = Collections.unmodifiableList(
                    Lists.newArrayList(
                            new LongToStringFieldSpecModifier(),
                            new BaseEnumFieldSpecModifier(),
                            new MybatisplusTypeHandlerFieldSpecModifier()
                    )
            );
        } else {
            throw new IllegalStateException();
        }
    }
}
