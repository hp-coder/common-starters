package com.luban.codegen.processor.repository.jpa;


import com.google.auto.service.AutoService;
import com.luban.codegen.processor.AbstractCodeGenProcessor;
import com.luban.codegen.processor.repository.GenRepository;
import com.luban.codegen.spi.CodeGenProcessor;
import com.luban.jpa.BaseRepository;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;

/**
 * @author hp
 */
@AutoService(value = CodeGenProcessor.class)
public class GenRepositoryProcessor extends AbstractCodeGenProcessor {

    public static final String REPOSITORY_SUFFIX = "Repository";

    @Override
    protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        String className = typeElement.getSimpleName() + REPOSITORY_SUFFIX;
        TypeSpec.Builder typeSpecBuilder = TypeSpec.interfaceBuilder(className)
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(BaseRepository.class), ClassName.get(typeElement), ClassName.get(Long.class)))
                .addModifiers(Modifier.PUBLIC);
        generateJavaSourceFile(generatePackage(typeElement), generatePath(typeElement), typeSpecBuilder);
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenRepository.class;
    }

    @Override
    public String generatePackage(TypeElement typeElement) {
        return typeElement.getAnnotation(GenRepository.class).pkgName();
    }

    @Override
    public String generatePath(TypeElement typeElement) {
        return typeElement.getAnnotation(GenRepository.class).sourcePath();
    }
}
