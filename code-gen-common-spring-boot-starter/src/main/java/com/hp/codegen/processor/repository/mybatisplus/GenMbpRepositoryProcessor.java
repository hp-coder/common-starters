package com.hp.codegen.processor.repository.mybatisplus;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.google.auto.service.AutoService;
import com.hp.codegen.constant.Orm;
import com.hp.codegen.processor.AbstractCodeGenProcessor;
import com.hp.codegen.processor.repository.GenRepository;
import com.hp.codegen.spi.CodeGenProcessor;
import com.squareup.javapoet.*;
import org.apache.ibatis.annotations.Mapper;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @author hp
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

        findByIdMethod(typeElement).ifPresent(typeSpecBuilder::addMethod);
        findAllByIdMethod(typeElement).ifPresent(typeSpecBuilder::addMethod);

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

    private Optional<MethodSpec> findByIdMethod(TypeElement typeElement) {
        return Optional.of(MethodSpec.methodBuilder("findById")
                .addParameter(Long.class, "id")
                .addModifiers(Modifier.DEFAULT, Modifier.PUBLIC)
                .addCode(CodeBlock.of("return $T.ofNullable(selectById(id));\n", ClassName.get(Optional.class)))
                .returns(ParameterizedTypeName.get(ClassName.get(Optional.class), ClassName.get(typeElement)))
                .build());
    }

    private Optional<MethodSpec> findAllByIdMethod(TypeElement typeElement) {
        return Optional.of(MethodSpec.methodBuilder("findAllById")
                .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(ClassName.get(Collection.class), ClassName.get(Long.class)), "ids").build())
                .addModifiers(Modifier.DEFAULT, Modifier.PUBLIC)
                .addCode(
                        CodeBlock.of(
                                "if ($T.isEmpty(ids)) { \n" +
                                        "   return $T.emptyList();\n" +
                                        "}\n",
                                ClassName.get(CollUtil.class),
                                ClassName.get(Collections.class)
                        )
                )
                .addCode(
                        CodeBlock.of("$T list = selectBatchIds(ids);\n",
                                ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get(typeElement))
                        )
                )
                .addCode(
                        CodeBlock.of(
                                "if ($T.isEmpty(list)) {\n" +
                                        "   return $T.emptyList();\n" +
                                        "}\n",
                                ClassName.get(CollUtil.class),
                                ClassName.get(Collections.class)
                        )
                )
                .addCode(CodeBlock.of("return list;"))
                .returns(ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get(typeElement)))
                .build());
    }

}
