package com.luban.sync.handler;

import com.luban.sync.SyncMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author hp
 */
@Slf4j
@Component
public class DefaultSyncHandler extends AbstractSyncHandler{
    @Override
    public void doSave(SyncMessage syncMessage) {
        log.info("Default sync handler: doSave invoked.");
    }

    @Override
    public void doDelete(SyncMessage syncMessage) {
        log.info("Default sync handler: doDelete invoked.");
    }

    @Override
    public void doReload(SyncMessage message) {
        log.info("Default sync handler: doReload invoked.");
    }
}
