
package com.hp.codegen.processor.infrastructure;


import cn.hutool.core.collection.CollUtil;
import com.google.auto.service.AutoService;
import com.google.common.collect.Lists;
import com.hp.codegen.annotation.domain.GenFindByPageQuery;
import com.hp.codegen.annotation.domain.GenQueryRepository;
import com.hp.codegen.annotation.infrastructure.GenDao;
import com.hp.codegen.annotation.infrastructure.GenMapperAdapter;
import com.hp.codegen.annotation.infrastructure.GenPo;
import com.hp.codegen.annotation.infrastructure.GenQueryRepositoryAdapter;
import com.hp.codegen.constant.Orm;
import com.hp.codegen.context.CodeGenContextHolder;
import com.hp.codegen.spi.CodeGenProcessor;
import com.hp.codegen.util.CodeGenHelper;
import com.hp.common.base.model.PageRequestWrapper;
import com.hp.common.base.model.PageResponse;
import com.hp.jpa.PageHelper;
import com.querydsl.core.BooleanBuilder;
import com.squareup.javapoet.*;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author hp
 */
@AutoService(value = CodeGenProcessor.class)
public class JpaBasedGenQueryRepositoryAdapterProcessor extends AbstractInfrastructureCodeGenProcessor {

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenQueryRepositoryAdapter.class;
    }

    @Override
    public boolean supportedOrm(Orm orm) {
        return Objects.equals(orm, Orm.SPRING_DATA_JPA);
    }

    @Override
    protected void customizeType(TypeElement typeElement, RoundEnvironment roundEnvironment, TypeSpec.Builder typeSpecBuilder) {
        typeSpecBuilder
                .addAnnotation(Component.class)
                .addAnnotation(RequiredArgsConstructor.class)
                .addSuperinterface(CodeGenContextHolder.getClassName(GenQueryRepository.class))
                .addModifiers(Modifier.PUBLIC);

        typeSpecBuilder.addFields(createInjectionFields());

        createFindByIdMethod().ifPresent(typeSpecBuilder::addMethod);
        createFindAllByIdMethod().ifPresent(typeSpecBuilder::addMethod);
        createFindByPageMethod().ifPresent(typeSpecBuilder::addMethod);
    }

    @Nonnull
    private static List<FieldSpec> createInjectionFields() {
        final List<FieldSpec> fieldSpecs = Lists.newArrayList();
        if (CodeGenContextHolder.isAnnotationPresent(GenDao.class)) {
            final FieldSpec serviceInjectionField = CodeGenHelper.createFieldSpecBuilder(GenDao.class)
                    .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                    .build();
            fieldSpecs.add(serviceInjectionField);
        }
        return fieldSpecs;
    }

    protected boolean methodNotCreatable(String methodName) {
        if (CodeGenContextHolder.missingAnyAnnotated(
                GenPo.class,
                GenMapperAdapter.class,
                GenDao.class
        )) {
            CodeGenContextHolder.log(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    String.format("To generate %s() on %s requires @GenPo @GenMapperAdapter and @GenDao.", methodName, this.currentGeneratingTypeName)
            );
            return true;
        }
        return false;
    }

    protected Optional<MethodSpec> createFindByIdMethod() {
        final String methodName = "find%sById".formatted(CodeGenContextHolder.getCurrentTypeName());
        if (methodNotCreatable(methodName)) {
            return Optional.empty();
        }

        final MethodSpec methodSpec = MethodSpec.methodBuilder(methodName)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(Long.class, "id")
                .addCode(
                        CodeBlock.of(
                                """
                                         return $L.findById(id).map($T.ADAPTER::poToEntity);
                                        """,
                                CodeGenContextHolder.getClassFieldName(GenDao.class),
                                CodeGenContextHolder.getClassName(GenMapperAdapter.class)
                        )
                )
                .returns(ParameterizedTypeName.get(ClassName.get(Optional.class), CodeGenContextHolder.getCurrentTypeClassName()))
                .build();

        return Optional.of(methodSpec);
    }

    protected Optional<MethodSpec> createFindAllByIdMethod() {
        final String methodName = "findAll%sById".formatted(CodeGenContextHolder.getCurrentTypeName());
        if (methodNotCreatable(methodName)) {
            return Optional.empty();
        }

        final MethodSpec methodSpec = MethodSpec.methodBuilder(methodName)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterizedTypeName.get(ClassName.get(Collection.class), ClassName.get(Long.class)), "ids")
                .addCode(
                        CodeBlock.of(
                                """
                                        final $T list = $L.findAllById(ids);
                                        if ($T.isEmpty(list)) {
                                            return $T.emptyList();
                                        }
                                        return list.stream().map($T.ADAPTER::poToEntity).collect($T.toList());
                                        """,
                                ParameterizedTypeName.get(ClassName.get(List.class), CodeGenContextHolder.getClassName(GenPo.class)),
                                CodeGenContextHolder.getClassFieldName(GenDao.class),
                                ClassName.get(CollUtil.class),
                                ClassName.get(Collections.class),
                                CodeGenContextHolder.getClassName(GenMapperAdapter.class),
                                ClassName.get(Collectors.class)
                        )
                )
                .returns(ParameterizedTypeName.get(ClassName.get(List.class), CodeGenContextHolder.getCurrentTypeClassName()))
                .build();

        return Optional.of(methodSpec);

    }

    protected Optional<MethodSpec> createFindByPageMethod() {
        final String methodName = "find%sByPage".formatted(CodeGenContextHolder.getCurrentTypeName());
        if (methodNotCreatable(methodName)) {
            return Optional.empty();
        }

        final MethodSpec methodSpec = MethodSpec.methodBuilder(methodName)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ParameterizedTypeName.get(ClassName.get(PageRequestWrapper.class), CodeGenContextHolder.getClassName(GenFindByPageQuery.class)), "queryWrapper")
                .addCode(
                        CodeBlock.of(
                                """
                                        final $T queryParams = queryWrapper.getQueryParams();
                                        final $T booleanBuilder = new $T();
                                                """,
                                CodeGenContextHolder.getClassName(GenFindByPageQuery.class),
                                ClassName.get(BooleanBuilder.class),
                                ClassName.get(BooleanBuilder.class)
                        )
                )
                .addCode(" // customize your query conditions here \n")
                .addCode(
                        CodeBlock.of(
                                """
                                         final $T page = $L.findAll(
                                                        booleanBuilder,
                                                        $T.toPage(queryWrapper)
                                                );
                                        """,
                                ParameterizedTypeName.get(ClassName.get(Page.class), CodeGenContextHolder.getClassName(GenPo.class)),
                                CodeGenContextHolder.getClassFieldName(GenDao.class),
                                ClassName.get(PageHelper.class)
                        )
                )
                .addCode(
                        CodeBlock.of(
                                """
                                        return $T.fromPage(page, $T.ADAPTER::poToEntity);
                                        """,
                                ClassName.get(PageHelper.class),
                                CodeGenContextHolder.getClassName(GenMapperAdapter.class)
                        )
                )
                .returns(ParameterizedTypeName.get(ClassName.get(PageResponse.class), CodeGenContextHolder.getCurrentTypeClassName()))
                .build();

        return Optional.of(methodSpec);

    }
}
