package com.luban.codegen.spi;

import com.luban.codegen.processor.OrmSupport;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;

/**
 * @author HP
 * @date 2022/10/24
 */
public interface CodeGenProcessor extends OrmSupport {

    Class<? extends Annotation> getAnnotation();

    String generatePackage(TypeElement typeElement);

    void generate(TypeElement typeElement, RoundEnvironment roundEnvironment);
}
