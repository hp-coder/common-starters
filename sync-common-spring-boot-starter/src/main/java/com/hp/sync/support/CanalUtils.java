package com.hp.sync.support;

import com.hp.sync.CanalMessage;
import com.hp.sync.SyncMessage;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author hp
 */
public final class CanalUtils {

    public static List<Long> getPks(CanalMessage canalMessage) {
        final List<String> pkNames = canalMessage.getPkNames();
        if (CollectionUtils.isEmpty(pkNames)) {
            return Collections.emptyList();
        }
        final String pk = pkNames.get(0);
        final List<Map<String, Object>> data = canalMessage.getData();
        if (CollectionUtils.isEmpty(data)) {
            return Collections.emptyList();
        }
        return data.stream().map(map -> {
                    if (map.containsKey(pk)) {
                        return Long.parseLong((String) map.get(pk));
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    public static Optional<SyncMessage<Long>> canalToSync(CanalMessage canalMessage) {
        final List<Long> pks = getPks(canalMessage);
        if (CollectionUtils.isEmpty(pks)) {
            return Optional.empty();
        }
        final SyncMessage<Long> syncMessage = new SyncMessage<>(canalMessage.getTable(), Constants.DML.valueOf(canalMessage.getType()));
        syncMessage.setPk(canalMessage.getPkNames().get(0));
        syncMessage.setPkValues(pks);
        return Optional.of(syncMessage);
    }

}
