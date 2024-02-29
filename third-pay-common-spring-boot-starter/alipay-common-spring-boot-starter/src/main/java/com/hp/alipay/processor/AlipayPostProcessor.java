package com.hp.alipay.processor;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author hp
 */
public interface AlipayPostProcessor<T, R> {

    Set<AlipayPostProcessor> PROCESSORS = new HashSet<>();

    Function<T, R> processNotify();

    Function<T, R> processReturn();

    Predicate<T> predication();

}
