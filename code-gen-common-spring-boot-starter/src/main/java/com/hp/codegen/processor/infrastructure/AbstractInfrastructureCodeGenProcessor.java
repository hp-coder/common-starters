package com.hp.codegen.processor.infrastructure;

import com.hp.codegen.annotation.infrastructure.GenInfrastructure;
import com.hp.codegen.processor.AbstractCodeGenProcessor;

import java.lang.annotation.Annotation;

/**
 * @author hp
 */
public abstract class AbstractInfrastructureCodeGenProcessor extends AbstractCodeGenProcessor {

    @Override
    public Class<? extends Annotation> getAnnotationGroup() {
        return GenInfrastructure.class;
    }
}
