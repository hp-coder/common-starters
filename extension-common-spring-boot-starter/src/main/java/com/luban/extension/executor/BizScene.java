package com.luban.extension.executor;

/**
 * 唯一的bizId ，这里为了更好的约定，bizId 的实现类最好使用枚举*
 * @author hp
 * @date 2022/10/19
 */
public interface BizScene {
    /**
     * @refer enum*
     * @return
     */
    String bizId();
}
