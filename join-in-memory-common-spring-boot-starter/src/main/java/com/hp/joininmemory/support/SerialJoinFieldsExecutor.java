package com.hp.joininmemory.support;

import com.hp.joininmemory.AfterJoinMethodExecutor;
import com.hp.joininmemory.JoinFieldExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.util.List;

/**
 * @author hp 2023/3/27
 */
@Slf4j
public class SerialJoinFieldsExecutor<DATA> extends AbstractJoinFieldsExecutor<DATA> {

    public SerialJoinFieldsExecutor(
            Class<DATA> clazz,
            List<JoinFieldExecutor<DATA>> joinFieldExecutors,
            List<AfterJoinMethodExecutor<DATA>> afterJoinMethodExecutors
    ) {
        super(clazz, joinFieldExecutors, afterJoinMethodExecutors);
    }

    @Override
    public void execute(List<DATA> dataList) {
        getJoinFieldExecutors()
                .forEach(executor -> {
                    if (log.isDebugEnabled()) {
                        StopWatch stopwatch = new StopWatch("Starting executing join tasks");
                        stopwatch.start();
                        executor.execute(dataList);
                        stopwatch.stop();
                        log.debug("run execute cost {} ms, executor is {}, data is {}.", stopwatch.getTotalTimeMillis(), executor, dataList);
                    } else {
                        executor.execute(dataList);
                    }
                });

        if (log.isDebugEnabled()) {
            StopWatch stopwatch = new StopWatch("Starting executing after join tasks");
            stopwatch.start();
            dataList.forEach(data ->
                    getAfterJoinMethodExecutors()
                            .forEach(
                                    e -> e.execute(data)
                            )
            );
            stopwatch.stop();
            log.debug("run execute cost {} ms, data is {}.", stopwatch.getTotalTimeMillis(), dataList);
        } else {
            dataList.forEach(data ->
                    getAfterJoinMethodExecutors()
                            .forEach(
                                    e -> e.execute(data)
                            )
            );
        }

    }
}
