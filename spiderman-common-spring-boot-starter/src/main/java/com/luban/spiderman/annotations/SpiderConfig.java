package com.luban.spiderman.annotations;

import com.luban.common.base.annotations.MethodDesc;
import com.luban.spiderman.constants.SpiderManExecutorType;
import org.jsoup.Connection;

import java.lang.annotation.*;

/**
 * @author hp
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SpiderConfig {

    @MethodDesc("源网站: SpEl Supported")
    String website();

    @MethodDesc("请求方法")
    Connection.Method method() default Connection.Method.GET;

    @MethodDesc("请求头配置")
    SpiderHeader[] headers() default {};

    @MethodDesc("分页配置")
    SpiderPage page() default @SpiderPage;

    @MethodDesc("执行方式")
    SpiderManExecutorType executorType() default SpiderManExecutorType.SINGLE;

    @MethodDesc("线程池名称")
    String executorName() default "defaultSpiderManExecutor";
}
