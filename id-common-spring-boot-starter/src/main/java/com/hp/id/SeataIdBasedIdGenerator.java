package com.hp.id;

import io.seata.common.util.IdWorker;
import org.springframework.lang.Nullable;

/**
 * @author hp
 */
public class SeataIdBasedIdGenerator implements IdGenerator {

    private final IdWorker idWorker;

    public SeataIdBasedIdGenerator(@Nullable Long workerId) {
        this.idWorker = new IdWorker(workerId);
    }

    @Override
    public long nextId() {
        return idWorker.nextId();
    }
}
