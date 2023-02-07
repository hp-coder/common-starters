package com.luban.extension.executor;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author HP
 * @date 2022/10/19
 */
public abstract class AbstractServiceExecutor implements ServiceExecutor {

    @Override
    public <S, R> R execute(Class<S> serivce, BizScene scene, Function<S, R> function) {
        final S s = selectService(scene, serivce);
        return function.apply(s);
    }

    @Override
    public <S> void execute(Class<S> service, BizScene scene, Consumer<S> consumer) {
        final S s = selectService(scene, service);
        consumer.accept(s);
    }

    protected abstract <S> S selectService(BizScene scene, Class<S> service);
}
