package com.hp.dingtalk.component.statemachine;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Function;

/**
 * @author hp 2023/3/28
 */
public interface IDingState<DATA> {

    /**
     * 状态名称
     *
     * @return 状态
     */
    String name();

    /**
     * 状态进入
     *
     * @param input 输入
     */
    void onEntry(DATA input);

    /**
     * 是否可以执行onEntry
     *
     * @return Predicate
     */
    Function<DATA, Predicate> proceedEntry();

    /**
     * 状态完成
     *
     * @param input 输入
     */
    void onComplete(DATA input);

    /**
     * 跳链操作
     * @param input 输入
     */
    IDingState<DATA> jumpAfterEntry(DATA input);

    IDingState<DATA> jumpAfterComplete(DATA input);

    /**
     * 是否可以执行onComplete
     *
     * @return Predicate
     */
    Function<DATA, Predicate> proceedComplete();

    /**
     * 当前状态处理的上下文
     * @return 上下文
     */
    IDingStateContext<?> getContext();

    /**
     * 完成状态机
     */
    void complete();

    @AllArgsConstructor
    @Getter
    class Predicate {
        /**
         * 校验是否通过
         */
        private final boolean proceed;
        /**
         * 不通过原因
         */
        private final String reason;
    }
}
