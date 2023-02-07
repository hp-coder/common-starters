package com.luban.extension.executor;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author HP
 * @date 2022/10/19
 */
public interface ServiceExecutor {

    <S> void execute(Class<S> service, BizScene scene, Consumer<S> consumer);

    <S,R> R execute(Class<S> serivce, BizScene scene, Function<S,R> function);
}
