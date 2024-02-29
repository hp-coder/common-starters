package com.hp.spiderman.annotations;

import com.hp.common.base.annotations.MethodDesc;

import java.lang.annotation.*;

/**
 * @author hp
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface SpiderPage {

    @MethodDesc("是否分页")
    boolean required() default false;

    @MethodDesc("分页参数名称:第几页")
    String pageParam() default "page";

    @MethodDesc("分页参数名称:每页记录数")
    String sizeParam() default "size";

    @MethodDesc("最大页码")
    int maximumPage() default 1000;
    @MethodDesc("默认每页10条")
    int maximumSize() default 10;
}
