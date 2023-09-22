package com.luban.codegen.processor.service.mybatisplus;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.auto.service.AutoService;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.luban.codegen.constant.Orm;
import com.luban.codegen.context.DefaultNameContext;
import com.luban.codegen.processor.AbstractCodeGenProcessor;
import com.luban.codegen.processor.service.GenServiceImpl;
import com.luban.codegen.spi.CodeGenProcessor;
import com.luban.codegen.util.StringUtils;
import com.luban.common.base.enums.CodeEnum;
import com.luban.common.base.exception.BusinessException;
import com.luban.common.base.model.PageRequestWrapper;
import com.luban.mybatisplus.EntityOperations;
import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @author hp
 */
@AutoService(value = CodeGenProcessor.class)
public class GenMbpServiceImplProcessor extends AbstractCodeGenProcessor {

    public static final String IMPL_SUFFIX = "ServiceImpl";

    @Override
    public boolean supportedOrm(Orm orm) {
        return Objects.equals(orm, Orm.MYBATIS_PLUS);
    }

    @Override
    protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        DefaultNameContext nameContext = getNameContext();
        String className = typeElement.getSimpleName() + IMPL_SUFFIX;
        TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(className)
                .superclass(
                        ParameterizedTypeName.get(
                                ClassName.get(ServiceImpl.class),
                                ClassName.get(nameContext.getRepositoryPackageName(), nameContext.getRepositoryClassName()),
                                ClassName.get(typeElement)
                        )
                )
                .addSuperinterface(
                        ClassName.get(nameContext.getServicePackageName(), nameContext.getServiceClassName()))
                .addAnnotation(Slf4j.class)
                .addAnnotation(Service.class)
                .addAnnotation(RequiredArgsConstructor.class)
                .addAnnotation(AnnotationSpec.builder(Transactional.class).addMember("rollbackFor", "$L", "Exception.class").build())
                .addModifiers(Modifier.PUBLIC);
        if (StringUtils.containsNull(nameContext.getRepositoryPackageName())) {
            return;
        }
        String repositoryFieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL,
                nameContext.getRepositoryClassName());

        FieldSpec repositoryField = FieldSpec
                .builder(ClassName.get(nameContext.getRepositoryPackageName(),
                        nameContext.getRepositoryClassName()), repositoryFieldName)
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build();
        typeSpecBuilder.addField(repositoryField);

        createMethod(typeElement, nameContext, repositoryFieldName).ifPresent(typeSpecBuilder::addMethod);
        updateMethod(typeElement, nameContext, repositoryFieldName).ifPresent(typeSpecBuilder::addMethod);
        enableMethod(typeElement, repositoryFieldName).ifPresent(typeSpecBuilder::addMethod);
        disableMethod(typeElement, repositoryFieldName).ifPresent(typeSpecBuilder::addMethod);
        findByIdMethod(typeElement, repositoryFieldName).ifPresent(typeSpecBuilder::addMethod);
        findAllByIdMethod(typeElement, repositoryFieldName).ifPresent(typeSpecBuilder::addMethod);
        findByPageMethod(typeElement, nameContext, repositoryFieldName).ifPresent(typeSpecBuilder::addMethod);

        generateJavaSourceFile(generatePackage(typeElement), generatePath(typeElement), typeSpecBuilder);
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenServiceImpl.class;
    }

    @Override
    public String generatePackage(TypeElement typeElement) {
        return typeElement.getAnnotation(GenServiceImpl.class).pkgName();
    }

    @Override
    public String generatePath(TypeElement typeElement) {
        return typeElement.getAnnotation(GenServiceImpl.class).sourcePath();
    }

    private Optional<MethodSpec> createMethod(
            TypeElement typeElement,
            DefaultNameContext nameContext,
            String repositoryFieldName
    ) {
        boolean containsNull = StringUtils.containsNull(nameContext.getDtoClassName(), nameContext.getMapperPackageName());
        if (!containsNull) {
            return Optional.of(MethodSpec.methodBuilder("create" + typeElement.getSimpleName())
                    .addParameter(ClassName.get(nameContext.getDtoPackageName(), nameContext.getDtoClassName()), "creator")
                    .addModifiers(Modifier.PUBLIC)
                    .addCode(
                            CodeBlock.of(
                                    "return $T.doCreate($L)\n" +
                                            ".create(() -> $T.INSTANCE.dtoToEntity(creator))\n" +
                                            ".update($L::init)\n" +
                                            ".execute()\n" +
                                            ".map($L::getId)\n" +
                                            ".orElseThrow(() -> new $T($T.SaveError));",
                                    EntityOperations.class,
                                    repositoryFieldName,
                                    ClassName.get(nameContext.getMapperPackageName(), nameContext.getMapperClassName()),
                                    typeElement.getSimpleName(),
                                    typeElement.getSimpleName(),
                                    ClassName.get(BusinessException.class),
                                    ClassName.get(CodeEnum.class)
                            )
                    )
                    .addAnnotation(Override.class)
                    .returns(Long.class).build());
        }
        return Optional.empty();
    }

    private Optional<MethodSpec> updateMethod(
            TypeElement typeElement,
            DefaultNameContext nameContext,
            String repositoryFieldName
    ) {
        boolean containsNull = StringUtils.containsNull(nameContext.getDtoPackageName());
        if (!containsNull) {
            return Optional.of(MethodSpec.methodBuilder("update" + typeElement.getSimpleName())
                    .addParameter(ClassName.get(nameContext.getDtoPackageName(), nameContext.getDtoClassName()), "updater")
                    .addModifiers(Modifier.PUBLIC)
                    .addCode(
                            CodeBlock.of(
                                    "$T.doUpdate($L)\n.loadById(updater.getId())\n"
                                            + ".update(updater::update$L)\n"
                                            + ".execute();",
                                    EntityOperations.class,
                                    repositoryFieldName,
                                    typeElement.getSimpleName()
                            )
                    )
                    .addAnnotation(Override.class)
                    .build());
        }
        return Optional.empty();
    }

    private Optional<MethodSpec> enableMethod(
            TypeElement typeElement,
            String repositoryFieldName
    ) {
        return Optional.of(MethodSpec.methodBuilder("enable" + typeElement.getSimpleName())
                .addParameter(Long.class, "id")
                .addModifiers(Modifier.PUBLIC)
                .addCode(
                        CodeBlock.of(
                                "$T.doUpdate($L)\n.loadById(id)\n"
                                        + ".update($L::valid)\n"
                                        + ".execute();",
                                EntityOperations.class,
                                repositoryFieldName,
                                typeElement.getSimpleName()
                        )
                )
                .addAnnotation(Override.class)
                .build());
    }

    private Optional<MethodSpec> disableMethod(
            TypeElement typeElement,
            String repositoryFieldName
    ) {
        return Optional.of(MethodSpec.methodBuilder("disable" + typeElement.getSimpleName())
                .addParameter(Long.class, "id")
                .addModifiers(Modifier.PUBLIC)
                .addCode(
                        CodeBlock.of(
                                "$T.doUpdate($L)\n.loadById(id)\n"
                                        + ".update($L::invalid)\n"
                                        + ".execute();",
                                EntityOperations.class,
                                repositoryFieldName,
                                typeElement.getSimpleName()
                        )
                )
                .addAnnotation(Override.class)
                .build());
    }

    private Optional<MethodSpec> findByIdMethod(
            TypeElement typeElement,
            String repositoryFieldName
    ) {

        return Optional.of(MethodSpec.methodBuilder("findById")
                .addParameter(Long.class, "id")
                .addModifiers(Modifier.PUBLIC)
                .addCode(
                        CodeBlock.of(
                                "return $L.findById(id).orElseThrow(() -> new $T($T.NotFindError));",
                                repositoryFieldName,
                                BusinessException.class,
                                CodeEnum.class
                        )
                )
                .addAnnotation(Override.class)
                .returns(ClassName.get(typeElement))
                .build());

    }

    private Optional<MethodSpec> findAllByIdMethod(
            TypeElement typeElement,
            String repositoryFieldName
    ) {
        return Optional.of(MethodSpec.methodBuilder("findAllById")
                .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(ClassName.get(Collection.class), ClassName.get(Long.class)), "ids").build())
                .addModifiers(Modifier.PUBLIC)
                .addCode(
                        CodeBlock.of(
                                "if ($T.isEmpty(ids)) {\n" +
                                        "   return $T.emptyList();\n" +
                                        "}\n",
                                ClassName.get(CollUtil.class),
                                ClassName.get(Collections.class)
                        )
                )
                .addCode(
                        CodeBlock.of(
                                "return $L.findAllById(ids);\n",
                                repositoryFieldName
                        )
                )
                .addAnnotation(Override.class)
                .returns(ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get(typeElement)))
                .build());
    }

    private Optional<MethodSpec> findByPageMethod(
            TypeElement typeElement,
            DefaultNameContext nameContext,
            String repositoryFieldName
    ) {
        boolean containsNull = StringUtils.containsNull(nameContext.getDtoPackageName(), nameContext.getVoPackageName());
        if (!containsNull) {
            return Optional.of(
                    MethodSpec.methodBuilder("findByPage")
                            .addParameter(ParameterizedTypeName.get(ClassName.get(PageRequestWrapper.class),
                                            ClassName.get(nameContext.getDtoPackageName(), nameContext.getDtoClassName())),
                                    "query")
                            .addModifiers(Modifier.PUBLIC)
                            .addCode(
                                    CodeBlock.of(
                                            "$T page = new $T<>(query.getPage(), query.getPageSize());\n" +
                                                    "page.setOptimizeCountSql(true);\n" +
                                                    "page.setOrders($T.newArrayList($T.desc(\"create_at\")));\n",
                                            ParameterizedTypeName.get(ClassName.get(Page.class), ClassName.get(typeElement)),
                                            Page.class,
                                            Lists.class,
                                            OrderItem.class
                                    )
                            )
                            .addCode(
                                    CodeBlock.of(
                                            "final $T wrapper = new $T<>();\n",
                                            ParameterizedTypeName.get(ClassName.get(LambdaQueryWrapper.class), ClassName.get(typeElement)),
                                            LambdaQueryWrapper.class
                                    )
                            )
                            .addCode(
                                    CodeBlock.of(
                                            "$L.selectPage(page, wrapper);\n",
                                            repositoryFieldName
                                    )
                            )
                            .addCode(
                                    CodeBlock.of("return page;")
                            )
                            .addAnnotation(Override.class)
                            .returns(ParameterizedTypeName.get(ClassName.get(Page.class), ClassName.get(typeElement)))
                            .build()
            );
        }
        return Optional.empty();
    }

}
