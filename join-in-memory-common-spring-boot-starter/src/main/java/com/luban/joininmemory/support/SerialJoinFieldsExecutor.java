package com.luban.joininmemory.support;

import com.google.common.base.Stopwatch;
import com.luban.joininmemory.JoinFieldExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author hp 2023/3/27
 */
@Slf4j
public class SerialJoinFieldsExecutor<DATA> extends AbstractJoinFieldsExecutor<DATA> {

    public SerialJoinFieldsExecutor(Class<DATA> clazz, List<JoinFieldExecutor<DATA>> executors) {
        super(clazz, executors);
    }

    @Override
    public void execute(List<DATA> dataList) {
        getJoinFieldExecutors()
                .forEach(executor -> {
                    if (log.isDebugEnabled()) {
                        Stopwatch stopwatch = Stopwatch.createStarted();
                        executor.execute(dataList);
                        stopwatch.stop();

                        log.debug("run execute cost {} ms, executor is {}, data is {}.",
                                stopwatch.elapsed(TimeUnit.MILLISECONDS), executor, dataList);
                    } else {
                        executor.execute(dataList);
                    }
                });
    }
}
