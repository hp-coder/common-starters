package com.hp.dingtalk.constant.minih5event;

import cn.hutool.core.util.ClassUtil;
import com.google.common.collect.Sets;
import com.hp.common.base.enums.BaseEnum;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author hp
 */
public class DingMiniH5EventHolder {
    private static final Set<Class<?>> CLASS_HOLDER = Sets.newHashSet();

    public static void init() {
        final Set<Class<?>> classes = ClassUtil.scanPackage("com.hp.dingtalk.constant.minih5event", clz -> {
            AtomicBoolean isBaseEnum = new AtomicBoolean(false);
            AtomicBoolean isDingMiniH5Event = new AtomicBoolean(false);
            Arrays.stream(clz.getInterfaces())
                    .forEach(type -> {
                        isBaseEnum.compareAndSet(false, type.equals(BaseEnum.class));
                        isDingMiniH5Event.compareAndSet(false, type.equals(DingMiniH5Event.class));
                    });
            return clz.isEnum() && isBaseEnum.get() && isDingMiniH5Event.get();
        });
        CLASS_HOLDER.addAll(classes);
    }

    public static Set<Class<?>> holder() {
        return Sets.newHashSet(CLASS_HOLDER.iterator());
    }
}
