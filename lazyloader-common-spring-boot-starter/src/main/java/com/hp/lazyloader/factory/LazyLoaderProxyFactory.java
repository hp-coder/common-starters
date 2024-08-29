package com.hp.lazyloader.factory;

/**
 * @author hp
 */
public interface LazyLoaderProxyFactory {

    <T> T createFor(T t);

}
