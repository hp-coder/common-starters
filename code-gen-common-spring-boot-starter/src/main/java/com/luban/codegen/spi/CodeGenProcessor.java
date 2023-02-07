package com.hp.codegen.spi;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;

/**
 * @author HP
 * @date 2022/10/24
 */
public interface CodeGenProcessor {

    Class<? extends Annotation> getAnnotation();

    String generatePackage(TypeElement typeElement);

    void generate(TypeElement typeElement, RoundEnvironment roundEnvironment);
}
