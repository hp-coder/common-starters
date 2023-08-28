package com.luban.codegen.processor.repository.mybatisplus;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.google.auto.service.AutoService;
import com.luban.codegen.constant.Orm;
import com.luban.codegen.processor.AbstractCodeGenProcessor;
import com.luban.codegen.processor.repository.GenRepository;
import com.luban.codegen.spi.CodeGenProcessor;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import org.apache.ibatis.annotations.Mapper;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.Objects;

/**
 * @author gim
 */
@AutoService(value = CodeGenProcessor.class)
public class GenMbpRepositoryProcessor extends AbstractCodeGenProcessor {

    public static final String REPOSITORY_SUFFIX = "Repository";

    @Override
    public boolean supportedOrm(Orm orm) {
        return Objects.equals(orm, Orm.MYBATIS_PLUS);
    }

    @Override
    protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        String className = typeElement.getSimpleName() + REPOSITORY_SUFFIX;
        TypeSpec.Builder typeSpecBuilder = TypeSpec.interfaceBuilder(className)
                .addAnnotation(Mapper.class)
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(BaseMapper.class), ClassName.get(typeElement)))
                .addModifiers(Modifier.PUBLIC);
        generateJavaSourceFile(generatePackage(typeElement), typeElement.getAnnotation(GenRepository.class).sourcePath(), typeSpecBuilder);
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenRepository.class;
    }

    @Override
    public String generatePackage(TypeElement typeElement) {
        return typeElement.getAnnotation(GenRepository.class).pkgName();
    }
}
