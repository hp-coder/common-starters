package com.hp.biz.logger.annotation;

import com.hp.common.base.annotation.Meta;
import com.hp.common.base.annotation.MethodDesc;
import org.intellij.lang.annotations.Language;

import java.lang.annotation.*;

/**
 * @author hp
 */
@Meta
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(value = BizLoggers.class)
public @interface BizLogger {

    @MethodDesc("业务描述, 应该针对接口级别")
    String title() default "";

    @Language("SpEL")
    @MethodDesc("业务序列号,用于追踪数据操作, 如: 订单编号或ID")
    String bizNo();

    @Language("SpEL")
    @MethodDesc("业务类型, 如: 订单")
    String type();

    @Language("SpEL")
    @MethodDesc("业务子类型, 如: 订单 toC, toB端的区分")
    String subType() default "";

    @Language("SpEL")
    @MethodDesc("日志范围, 如: 客户可见, 业务人员可见等区分")
    String scope() default "";

    @Language("SpEL")
    @MethodDesc("业务成功日志模版, 业务正常执行无异常抛出时为成功, 此时上下文呢中可以获取BizLoggerProperties.returnValueKey对应的方法返回值用于构造成功日志")
    String successLog();

    @Language("SpEL")
    @MethodDesc("业务失败日志模版, 业务抛出异常时判断为失败: 此时上下文中可以获取BizLoggerProperties.throwableKey对应的异常信息用于构造异常日志")
    String errorLog() default "";

    @Language("SpEL")
    @MethodDesc("日志同步条件, 只接受boolean值. 如: true记录日志, false不记录日志")
    String condition() default "";

    @MethodDesc("在业务方法执行前/后执行日志记录, 之前=true, 之后=false, 之前场景下, 无法获取业务方法内设置的上下文变量")
    boolean preInvocation() default false;

    BizDiffer diff() default @BizDiffer(ignored = true);

    @MethodDesc("处理/同步顺序")
    int order() default -1;
}
