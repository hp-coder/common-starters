package com.hp.biz.logger.service;

import org.springframework.util.StopWatch;

/**
 * @author hp
 */
@FunctionalInterface
public interface IBizLoggerPerformanceMonitor {

    String MONITOR_NAME = "biz-logger-performance";

    String MONITOR_TASK_SYNC_LOGS = "sync-logs";

    String MONITOR_TASK_BEFORE_INVOCATION = "before-invocation";

    String MONITOR_TASK_INVOCATION = "invocation";

    String MONITOR_TASK_AFTER_INVOCATION = "after-invocation";

    void print(StopWatch stopWatch);
}
