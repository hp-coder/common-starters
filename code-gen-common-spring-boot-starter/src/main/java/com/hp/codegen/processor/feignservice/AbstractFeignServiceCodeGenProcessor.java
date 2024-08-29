package com.hp.codegen.processor.feignservice;

import com.hp.codegen.processor.AbstractCodeGenProcessor;
import com.hp.codegen.annotation.feignservice.GenFeignService;

import java.lang.annotation.Annotation;

/**
 * @author hp
 */
public abstract class AbstractFeignServiceCodeGenProcessor extends AbstractCodeGenProcessor {

    @Override
    public Class<? extends Annotation> getAnnotationGroup() {
        return GenFeignService.class;
    }
}
