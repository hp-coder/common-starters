package com.hp.biz.logger.annotation;

import cn.hutool.extra.spring.EnableSpringUtil;
import com.hp.biz.logger.BizLoggerConfigureSelector;
import com.hp.common.base.annotation.MethodDesc;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(BizLoggerConfigureSelector.class)
@EnableSpringUtil
public @interface EnableBizLogger {

    @MethodDesc("租户信息, 应用或业务隔离")
    String tenant();

    AdviceMode mode() default AdviceMode.PROXY;

    @MethodDesc("创建日志对象执行方式")
    BizLoggerExecutorType executorType() default BizLoggerExecutorType.SERIAL;

    @MethodDesc("创建日志对象执行线程池Bean名称")
    String executorName() default "defaultBizLoggerExecutor";

    @MethodDesc("使用@BizLoggerComponent/@BizLoggerFunction 自定义函数注册时, 重复函数名称是否覆盖")
    boolean overrideFunction() default false;

    enum BizLoggerExecutorType {
        SERIAL,
        PARALLEL
    }
}
