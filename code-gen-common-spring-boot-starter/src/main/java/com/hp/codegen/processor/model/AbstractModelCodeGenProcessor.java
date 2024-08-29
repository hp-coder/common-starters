package com.hp.codegen.processor.model;

import com.hp.codegen.annotation.model.GenModel;
import com.hp.codegen.processor.AbstractCodeGenProcessor;

import java.lang.annotation.Annotation;

/**
 * @author hp
 */
public abstract class AbstractModelCodeGenProcessor extends AbstractCodeGenProcessor {

    @Override
    public Class<? extends Annotation> getAnnotationGroup() {
        return GenModel.class;
    }
}
