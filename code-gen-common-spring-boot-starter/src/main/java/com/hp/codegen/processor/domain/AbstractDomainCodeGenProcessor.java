package com.hp.codegen.processor.domain;

import com.hp.codegen.annotation.domain.GenDomain;
import com.hp.codegen.processor.AbstractCodeGenProcessor;

import java.lang.annotation.Annotation;

/**
 * @author hp
 */
public abstract class AbstractDomainCodeGenProcessor extends AbstractCodeGenProcessor {

    @Override
    public Class<? extends Annotation> getAnnotationGroup() {
        return GenDomain.class;
    }
}
