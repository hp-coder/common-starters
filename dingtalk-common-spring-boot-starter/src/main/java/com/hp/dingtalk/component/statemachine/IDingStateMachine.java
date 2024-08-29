package com.hp.dingtalk.component.statemachine;

/**
 * @author hp 2023/3/28
 */
public interface IDingStateMachine<DATA> {

    /**
     * 获取状态机当前状态
     *
     * @return 当前状态
     */
    IDingState<DATA> currentState();

    /**
     * 下一个状态
     *
     * @return 下一个状态
     */
    IDingState<DATA> nextState(DATA input);

    /**
     * 状态改变
     *
     * @param newState 下一个状态
     * @param input    输入数据
     */
    void stateChange(IDingState<DATA> newState, DATA input);

    /**
     * 计算输入
     *
     * @param input 输入
     */
    void computeInput(DATA input);

    /**
     * 统一的退出方法，需要实现为在任何时候调用及退出和销毁该状态机
     * @param exitState 退出时的状态
     * @param input 输入
     */
    void exit(IDingState<DATA> exitState, DATA input);
}
