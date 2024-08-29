package com.hp.codegen.processor.api;

import com.hp.codegen.annotation.api.GenApi;
import com.hp.codegen.processor.AbstractCodeGenProcessor;

import java.lang.annotation.Annotation;

/**
 * @author hp
 */
public abstract class AbstractApiCodeGenProcessor extends AbstractCodeGenProcessor {

    @Override
    public Class<? extends Annotation> getAnnotationGroup() {
        return GenApi.class;
    }
}
