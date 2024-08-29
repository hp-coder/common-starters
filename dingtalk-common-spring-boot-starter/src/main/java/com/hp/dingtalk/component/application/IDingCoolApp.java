package com.hp.dingtalk.component.application;

/**
 * 酷应用定义，微应用内部的一个配置项，暂时先这样
 * @author hp 2023/3/21
 */
public interface IDingCoolApp extends IDingMiniH5 {
    /**
     * 酷应用编码
     */
    String getCoolAppCode();
}
