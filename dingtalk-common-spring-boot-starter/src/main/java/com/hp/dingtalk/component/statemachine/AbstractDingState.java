package com.hp.dingtalk.component.statemachine;

import lombok.Getter;

import java.util.regex.Pattern;

/**
 * @author hp 2023/3/28
 */
public abstract class AbstractDingState<DATA> implements IDingState<DATA> {

    protected static final Pattern SKIP = Pattern.compile("^(?i)skip$|^跳过$");

    @Getter
    protected final IDingStateContext<?> context;

    public AbstractDingState(IDingStateContext<?> context) {
        this.context = context;
    }

    /**
     * 子类处理进入状态时数据处理
     *
     * @param input 输入数据
     */
    protected abstract void handleEntry(DATA input);

    /**
     * 子类处理完成状态时数据处理
     *
     * @param input 输入数据
     */
    protected abstract void handleComplete(DATA input);

    @Override
    public void onEntry(DATA input) {
        handleEntry(input);
    }

    @Override
    public void onComplete(DATA input) {
        handleComplete(input);
    }

    @Override
    public void complete() {
        this.context.setComplete(true);
        this.context.getApplicationContext().publishEvent(new IDingStateEvents.CompleteEvent(this.context));
    }

    @Override
    public IDingState<DATA> jumpAfterEntry(DATA input) {
        return null;
    }

    @Override
    public IDingState<DATA> jumpAfterComplete(DATA input) {
        return null;
    }
}
