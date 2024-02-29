package com.hp.spiderman.annotations;

import com.hp.common.base.annotations.MethodDesc;

import java.lang.annotation.*;

/**
 * 查找dom元素节点
 *
 * @author hp
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SpiderNode {
    @MethodDesc("DOM元素")
    String node();

    @MethodDesc("查询node方法")
    SpiderNodeFilter[] filters() default {};

    @MethodDesc("如何选择查询到的节点")
    SpiderNodeRetriever nodeRetriever() default @SpiderNodeRetriever;

    @MethodDesc("如何获取值: SpEL supported")
    String retriever() default "";

    @MethodDesc("如何转换值: SpEL supported")
    String converter() default "";

    @MethodDesc("异步时优先级分组")
    int runOnLevel() default 0;
}
