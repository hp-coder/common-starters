package com.hp.gc.collector;

import org.springframework.beans.factory.InitializingBean;

import javax.management.NotificationEmitter;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;

/**
 * @author hp
 */
public class GCLogNotificationRegister implements InitializingBean {

    private static List<GarbageCollectorMXBean> beans = ManagementFactory.getGarbageCollectorMXBeans();

    @Override
    public void afterPropertiesSet() {
        for (GarbageCollectorMXBean garbageCollectorMXBean : beans) {
            final NotificationEmitter notificationEmitter = (NotificationEmitter) garbageCollectorMXBean;
            GCLogNotificationListener notificationListener = new GCLogNotificationListener();
            GCLogNotificationFilter notificationFilter = new GCLogNotificationFilter();

            notificationEmitter.addNotificationListener(notificationListener, notificationFilter, garbageCollectorMXBean);//注册监听器、通知过滤器
        }
    }
}
