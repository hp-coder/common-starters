package com.hp.codegen.processor.app;

import com.hp.codegen.annotation.app.GenApp;
import com.hp.codegen.processor.AbstractCodeGenProcessor;

import java.lang.annotation.Annotation;

/**
 * @author hp
 */
public abstract class AbstractAppCodeGenProcessor extends AbstractCodeGenProcessor {

    @Override
    public Class<? extends Annotation> getAnnotationGroup() {
        return GenApp.class;
    }
}
