package com.hp.extension.executor;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author hp
 * @date 2022/10/19
 */
public abstract class AbstractServiceExecutor implements ServiceExecutor {

    @Override
    public <S> void execute(Class<S> service, BizScene scene, Consumer<S> consumer) {
        final S s = selectService(scene, service);
        consumer.accept(s);
    }

    @Override
    public <S, R> R execute(Class<S> service, BizScene scene, Function<S, R> function) {
        final S s = selectService(scene, service);
        return function.apply(s);
    }

    protected abstract <S> S selectService(BizScene scene, Class<S> service);
}
