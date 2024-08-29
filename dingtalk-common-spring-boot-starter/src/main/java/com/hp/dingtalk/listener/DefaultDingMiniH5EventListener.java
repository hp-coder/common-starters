package com.hp.dingtalk.listener;

import com.hp.dingtalk.constant.minih5event.DingMiniH5Event;
import com.hp.dingtalk.pojo.callback.event.DingMiniH5CallbackEvents;
import com.hp.dingtalk.service.callback.minih5.IDingMiniH5EventCallbackHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * 默认的钉钉微应用事件订阅-系统内事件监听器
 * 默认使用异步处理任务
 *
 * @author hp
 */
@Slf4j
public class DefaultDingMiniH5EventListener {

    private final List<IDingMiniH5EventCallbackHandler> miniH5EventCallbackHandlers;
    private final ExecutorService defaultDingTalkExecutor;

    public DefaultDingMiniH5EventListener(
            List<IDingMiniH5EventCallbackHandler> miniH5EventCallbackHandlers,
            @Qualifier("defaultDingTalkExecutor") ExecutorService defaultDingTalkExecutor
    ) {
        this.miniH5EventCallbackHandlers = miniH5EventCallbackHandlers;
        this.defaultDingTalkExecutor = defaultDingTalkExecutor;
    }

    /**
     * 注意幂等
     */
//    @Async
    @EventListener
    public void handleMiniH5EventCallback(DingMiniH5CallbackEvents.DingMiniH5EventDecryptedPayload payload) {
        final DingMiniH5Event eventType = payload.getEventType();
        log.info("使用插件自带默认监听器处理钉钉微应用事件回调:{}({})", eventType.getCode(), eventType.getName());
        if (CollectionUtils.isEmpty(miniH5EventCallbackHandlers)) {
            log.error("容器中没有找到任何IDingMiniH5EventCallbackHandler实例");
            return;
        }
        miniH5EventCallbackHandlers
                .stream()
                .filter(handler -> handler.support(payload))
                .collect(Collectors.groupingBy(IDingMiniH5EventCallbackHandler::order))
                .entrySet()
                .stream()
                .map(entry -> new ExecutorWithLevel(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(ExecutorWithLevel::getOrder))
                .forEach(executorWithLevel -> {
                    try {
                        final List<Task> tasks = executorWithLevel.handlers.stream()
                                .map(i -> new Task(payload, i))
                                .collect(Collectors.toList());
                        tasks.forEach(defaultDingTalkExecutor::submit);
                    } catch (Exception e) {
                        log.error("执行对钉钉微应用事件:{}({})异常", eventType.getCode(), eventType.getName(), e);
                    }
                });
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class ExecutorWithLevel {
        Integer order;
        List<IDingMiniH5EventCallbackHandler> handlers;
    }

    static class Task implements Callable<Void> {
        private final DingMiniH5CallbackEvents.DingMiniH5EventDecryptedPayload payload;
        private final IDingMiniH5EventCallbackHandler handler;

        public Task(DingMiniH5CallbackEvents.DingMiniH5EventDecryptedPayload payload, IDingMiniH5EventCallbackHandler handler) {
            this.payload = payload;
            this.handler = handler;
        }

        @Override
        public Void call() {
            final boolean process = handler.process(payload);
            log.debug("钉钉微应用事件处理器处理结果:{}", process);
            return null;
        }
    }
}
