package com.luban.spiderman.annotations;

import com.luban.common.base.annotations.MethodDesc;
import com.luban.spiderman.constants.NodeRetrieveLogic;

import java.lang.annotation.*;

/**
 * @author hp
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface SpiderNodeRetriever {

    NodeRetrieveLogic logic() default NodeRetrieveLogic.SELF;

    @MethodDesc("实际html渲染后人可见的节点之间都存在一个不可见的节点")
    int childIndex() default 0;
}
