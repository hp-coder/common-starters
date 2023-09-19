package com.luban.codegen.context;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;

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
}
