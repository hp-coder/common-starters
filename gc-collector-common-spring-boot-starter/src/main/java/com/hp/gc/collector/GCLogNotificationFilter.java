package com.hp.gc.collector;

import com.sun.management.GarbageCollectionNotificationInfo;

import javax.management.Notification;
import javax.management.NotificationFilter;
import java.io.Serial;

/**
 * @author hp
 */
public class GCLogNotificationFilter implements NotificationFilter {
    @Serial
    private static final long serialVersionUID = -6971150115604091770L;

    @Override
    public boolean isNotificationEnabled(Notification notification) {
        return GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION.equals(notification.getType());
    }
}
