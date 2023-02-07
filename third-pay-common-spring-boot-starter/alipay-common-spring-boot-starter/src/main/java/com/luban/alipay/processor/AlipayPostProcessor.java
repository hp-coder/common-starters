package com.luban.alipay.processor;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author HP 2022/11/14
 */
public interface AlipayPostProcessor<T, R> {

    Set<AlipayPostProcessor> PROCESSORS = new HashSet<>();

    Function<T, R> processNotify();

    Function<T, R> processReturn();

    Predicate<T> predication();

}
