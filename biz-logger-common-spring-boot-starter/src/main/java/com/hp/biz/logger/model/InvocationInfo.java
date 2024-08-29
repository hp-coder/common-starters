package com.hp.biz.logger.model;

import com.hp.common.base.annotation.FieldDesc;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

/**
 * @author hp
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvocationInfo {

    @FieldDesc("方法所在类")
    private Class<?> targetClass;

    @FieldDesc("调用方法")
    private Method method;

    @FieldDesc("方法入参")
    private Object[] args;

    @FieldDesc("调用是否成功")
    private boolean success = false;

    @FieldDesc("方法出参")
    private Object result = null;

    @FieldDesc("方法抛出的异常")
    private Throwable throwable;

    @FieldDesc("方法调用耗时(毫秒)")
    private Long timeCost;
}
