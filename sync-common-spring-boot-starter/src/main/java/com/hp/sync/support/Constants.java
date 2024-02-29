package com.hp.sync.support;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class Constants {

    public static class MQ {
        public static final String CANAL_EXCHANGE = "canal.exchange.direct";
        public static final String CANAL_QUEUE = "canal.process.queue";
        public static final String CANAL_ROUTING = "canal-routing";

        public static final String SYNC_EXCHANGE = "sync.exchange.direct";
        public static final String SYNC_QUEUE = "sync.process.queue";
        public static final String SYNC_ROUTING = "sync-routing";
    }

    @Getter
    @AllArgsConstructor
    public enum DML {
        INSERT,
        UPDATE,
        DELETE,
        TRUNCATE,
        RENAME,
        ERASE,
        ALTER,
        ;
    }

}
