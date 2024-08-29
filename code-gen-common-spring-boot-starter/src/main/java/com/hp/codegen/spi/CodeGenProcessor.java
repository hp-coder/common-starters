package com.hp.codegen.spi;

import com.hp.codegen.processor.OrmSupport;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;

/**
 * @author hp
 * @date 2022/10/24
 */
public interface CodeGenProcessor extends OrmSupport {

    void init(TypeElement typeElement, RoundEnvironment roundEnvironment);

    Class<? extends Annotation> getAnnotation();

    Class<? extends Annotation> getAnnotationGroup();

    void generate(TypeElement typeElement, RoundEnvironment roundEnvironment);
}
