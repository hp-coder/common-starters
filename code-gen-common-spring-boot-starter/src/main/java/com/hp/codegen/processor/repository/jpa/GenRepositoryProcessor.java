package com.hp.codegen.processor.repository.jpa;


import com.google.auto.service.AutoService;
import com.hp.codegen.constant.Orm;
import com.hp.codegen.processor.AbstractCodeGenProcessor;
import com.hp.codegen.processor.repository.GenRepository;
import com.hp.codegen.spi.CodeGenProcessor;
import com.hp.jpa.BaseRepository;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import org.springframework.stereotype.Repository;

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
    public boolean supportedOrm(Orm orm) {
        return Orm.SPRING_DATA_JPA.equals(orm);
    }

    @Override
    protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        String className = typeElement.getSimpleName() + REPOSITORY_SUFFIX;
        TypeSpec.Builder typeSpecBuilder = TypeSpec.interfaceBuilder(className)
                .addAnnotation(Repository.class)
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
