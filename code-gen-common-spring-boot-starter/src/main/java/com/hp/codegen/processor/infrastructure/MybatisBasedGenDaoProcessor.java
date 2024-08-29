package com.hp.codegen.processor.infrastructure;


import cn.hutool.core.collection.CollUtil;
import com.google.auto.service.AutoService;
import com.hp.codegen.annotation.infrastructure.GenDao;
import com.hp.codegen.annotation.infrastructure.GenPo;
import com.hp.codegen.constant.Orm;
import com.hp.codegen.context.CodeGenContextHolder;
import com.hp.codegen.spi.CodeGenProcessor;
import com.hp.mybatisplus.BaseRepository;
import com.squareup.javapoet.*;
import org.apache.ibatis.annotations.Mapper;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @author hp
 */
@AutoService(value = CodeGenProcessor.class)
public class MybatisBasedGenDaoProcessor extends AbstractInfrastructureCodeGenProcessor {

    @Override
    protected boolean isInterface() {
        return true;
    }

    @Override
    public boolean supportedOrm(Orm orm) {
        return Objects.equals(orm, Orm.MYBATIS_PLUS);
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenDao.class;
    }

    @Override
    protected boolean creatable(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        if (!CodeGenContextHolder.isAnnotationPresent(GenPo.class)) {
            CodeGenContextHolder.log(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    String.format("To generate %s requires @GenPo.", this.currentGeneratingTypeName)
            );
            return false;
        }
        return true;
    }

    @Override
    protected void customizeType(TypeElement typeElement, RoundEnvironment roundEnvironment, TypeSpec.Builder typeSpecBuilder) {
        typeSpecBuilder
                .addAnnotation(Mapper.class)
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(BaseRepository.class), CodeGenContextHolder.getClassName(GenPo.class), ClassName.get(Long.class)))
                .addModifiers(Modifier.PUBLIC);

        createFindByIdMethod().ifPresent(typeSpecBuilder::addMethod);
        createFindAllByIdMethod().ifPresent(typeSpecBuilder::addMethod);
    }

    private Optional<MethodSpec> createFindByIdMethod() {
        final String methodName = "findById";
        final MethodSpec methodSpec = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.DEFAULT, Modifier.PUBLIC)
                .addParameter(Long.class, "id")
                .addCode(CodeBlock.of("return $T.ofNullable(selectById(id));\n", ClassName.get(Optional.class)))
                .returns(ParameterizedTypeName.get(ClassName.get(Optional.class), CodeGenContextHolder.getClassName(GenPo.class)))
                .build();

        return Optional.of(methodSpec);
    }

    private Optional<MethodSpec> createFindAllByIdMethod() {
        final String methodName = "findAllById";
        final MethodSpec methodSpec = MethodSpec.methodBuilder(methodName)
                .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(ClassName.get(Collection.class), ClassName.get(Long.class)), "ids").build())
                .addModifiers(Modifier.DEFAULT, Modifier.PUBLIC)
                .addCode(
                        CodeBlock.of(
                                """
                                        if ($T.isEmpty(ids)) {
                                           return $T.emptyList();
                                        }
                                        """,
                                ClassName.get(CollUtil.class),
                                ClassName.get(Collections.class)
                        )
                )
                .addCode(
                        CodeBlock.of("return selectBatchIds(ids);")
                )
                .returns(ParameterizedTypeName.get(ClassName.get(List.class), CodeGenContextHolder.getClassName(GenPo.class)))
                .build();

        return Optional.of(methodSpec);
    }
}
