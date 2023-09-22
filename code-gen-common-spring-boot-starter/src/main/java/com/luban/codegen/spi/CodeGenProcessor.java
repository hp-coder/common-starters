package com.luban.codegen.spi;

import com.luban.codegen.processor.OrmSupport;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;

/**
 * @author hp
 * @date 2022/10/24
 */
public interface CodeGenProcessor extends OrmSupport {

    Class<? extends Annotation> getAnnotation();

    String generatePackage(TypeElement typeElement);

    default String generatePath(TypeElement typeElement){
        return null;
    }

    void generate(TypeElement typeElement, RoundEnvironment roundEnvironment);
}
