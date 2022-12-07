package com.hp.sync.converter;

import com.hp.sync.CanalMessage;
import com.hp.sync.SyncMessage;
import com.hp.sync.annotation.SyncTable;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author HP 2022/12/7
 */
public interface CanalConverter extends InitializingBean {
    ConcurrentHashMap<String, CanalConverter> HOLDER = new ConcurrentHashMap<>(16);

    static CanalConverter converter(String tableName) {
        return HOLDER.get(tableName);
    }

     List<SyncMessage> convert(CanalMessage canalMessage);

    @Override
    default void afterPropertiesSet() throws Exception {
        final SyncTable annotation = this.getClass().getAnnotation(SyncTable.class);
        if (annotation != null) {
            HOLDER.put(annotation.value(), this);
        }
    }
}
