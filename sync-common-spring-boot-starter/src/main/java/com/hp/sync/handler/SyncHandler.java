package com.hp.sync.handler;

import com.hp.sync.SyncMessage;
import com.hp.sync.annotation.SyncTable;
import org.springframework.beans.factory.InitializingBean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author HP 2022/12/7
 */
public interface SyncHandler extends InitializingBean {
    Map<String, SyncHandler> HOLDER = new ConcurrentHashMap<>(16);

    void handle(SyncMessage syncMessage);

    static SyncHandler handler(String tableName) {
        return HOLDER.get(tableName);
    }

    @Override
    default void afterPropertiesSet() {
        final Class<? extends SyncHandler> aClass = this.getClass();
        final SyncTable syncTable = aClass.getAnnotation(SyncTable.class);
        if (syncTable != null) {
            HOLDER.put(syncTable.value(), this);
        }
    }

}
