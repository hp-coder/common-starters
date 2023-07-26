package com.luban.codegen.processor.service;


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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author gim 获取名称时可以先获取上下文再取，不用一个个的取，这样更方便
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
        DefaultNameContext nameContext = getNameContext(typeElement);
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
                .addModifiers(Modifier.PUBLIC);
        if (StringUtils.containsNull(nameContext.getRepositoryPackageName())) {
            return;
        }
        String repositoryFieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL,
                nameContext.getRepositoryClassName());
        String classFieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL,
                typeElement.getSimpleName().toString());
        FieldSpec repositoryField = FieldSpec
                .builder(ClassName.get(nameContext.getRepositoryPackageName(),
                        nameContext.getRepositoryClassName()), repositoryFieldName)
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build();
        typeSpecBuilder.addField(repositoryField);
        Optional<MethodSpec> createMethod = createMethod(typeElement, nameContext, repositoryFieldName, classFieldName);
        createMethod.ifPresent(typeSpecBuilder::addMethod);

        Optional<MethodSpec> updateMethod = updateMethod(typeElement, nameContext, repositoryFieldName);
        updateMethod.ifPresent(typeSpecBuilder::addMethod);

        Optional<MethodSpec> validMethod = validMethod(typeElement, repositoryFieldName);
        validMethod.ifPresent(typeSpecBuilder::addMethod);

        Optional<MethodSpec> invalidMethod = invalidMethod(typeElement, repositoryFieldName);
        invalidMethod.ifPresent(typeSpecBuilder::addMethod);

        Optional<MethodSpec> findByIdMethod = findByIdMethod(typeElement, nameContext, repositoryFieldName, classFieldName);
        findByIdMethod.ifPresent(typeSpecBuilder::addMethod);

        Optional<MethodSpec> findByPageMethod = findByPageMethod(typeElement, nameContext, repositoryFieldName);
        findByPageMethod.ifPresent(typeSpecBuilder::addMethod);

        generateJavaSourceFile(generatePackage(typeElement), typeElement.getAnnotation(GenServiceImpl.class).sourcePath(), typeSpecBuilder);
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenServiceImpl.class;
    }

    @Override
    public String generatePackage(TypeElement typeElement) {
        return typeElement.getAnnotation(GenServiceImpl.class).pkgName();
    }

    private Optional<MethodSpec> createMethod(TypeElement typeElement, DefaultNameContext nameContext,
                                              String repositoryFieldName, String classFieldName) {
        boolean containsNull = StringUtils.containsNull(nameContext.getDtoClassName(), nameContext.getMapperPackageName());
        if (!containsNull) {
            return Optional.of(MethodSpec.methodBuilder("create" + typeElement.getSimpleName())
                    .addAnnotation(AnnotationSpec.builder(Transactional.class)
                            .addMember("rollbackFor", "$L", "Exception.class").build())
                    .addParameter(
                            ClassName.get(nameContext.getDtoPackageName(), nameContext.getDtoClassName()),
                            "creator")
                    .addModifiers(Modifier.PUBLIC)
                    .addCode(
                            CodeBlock.of(
                                    "Optional<$T> $L = $T.doCreate($L)\n.create(() -> $T.INSTANCE.dtoToEntity(creator))\n"
                                            + ".update($L::init)\n"
                                            + ".execute();\n",
                                    typeElement, classFieldName, EntityOperations.class, repositoryFieldName,
                                    ClassName.get(nameContext.getMapperPackageName(), nameContext.getMapperClassName()),
                                    typeElement.getSimpleName()
                            )
                    )
                    .addCode(
                            CodeBlock.of("return $L.map($L::getId).orElse(null);", classFieldName, typeElement.getSimpleName())
                    )
                    .addAnnotation(Override.class)
                    .returns(Long.class).build());
        }
        return Optional.empty();
    }

    private Optional<MethodSpec> updateMethod(TypeElement typeElement, DefaultNameContext nameContext,
                                              String repositoryFieldName) {
        boolean containsNull = StringUtils.containsNull(nameContext.getDtoPackageName());
        if (!containsNull) {
            return Optional.of(MethodSpec.methodBuilder("update" + typeElement.getSimpleName())
                    .addAnnotation(AnnotationSpec.builder(Transactional.class)
                            .addMember("rollbackFor", "$L", "Exception.class").build())
                    .addParameter(
                            ClassName.get(nameContext.getDtoPackageName(), nameContext.getDtoClassName()),
                            "updater")
                    .addModifiers(Modifier.PUBLIC)
                    .addCode(
                            CodeBlock.of("$T.doUpdate($L)\n.loadById(updater.getId())\n"
                                            + ".update(updater::update$L)\n"
                                            + ".execute();",
                                    EntityOperations.class, repositoryFieldName, typeElement.getSimpleName())
                    )
                    .addAnnotation(Override.class)
                    .build());
        }
        return Optional.empty();
    }

    private Optional<MethodSpec> validMethod(TypeElement typeElement, String repositoryFieldName) {
        return Optional.of(MethodSpec.methodBuilder("valid" + typeElement.getSimpleName())
                .addParameter(Long.class, "id")
                .addAnnotation(AnnotationSpec.builder(Transactional.class)
                        .addMember("rollbackFor", "$L", "Exception.class").build())
                .addModifiers(Modifier.PUBLIC)
                .addCode(
                        CodeBlock.of("$T.doUpdate($L)\n.loadById(id)\n"
                                        + ".update($L::valid)\n"
                                        + ".execute();",
                                EntityOperations.class, repositoryFieldName, typeElement.getSimpleName())
                )
                .addAnnotation(Override.class)
                .build());
    }

    private Optional<MethodSpec> invalidMethod(TypeElement typeElement, String repositoryFieldName) {
        return Optional.of(MethodSpec.methodBuilder("invalid" + typeElement.getSimpleName())
                .addParameter(Long.class, "id")
                .addAnnotation(AnnotationSpec.builder(Transactional.class)
                        .addMember("rollbackFor", "$L", "Exception.class").build())
                .addModifiers(Modifier.PUBLIC)
                .addCode(
                        CodeBlock.of("$T.doUpdate($L)\n.loadById(id)\n"
                                        + ".update($L::invalid)\n"
                                        + ".execute();",
                                EntityOperations.class, repositoryFieldName, typeElement.getSimpleName())
                )
                .addAnnotation(Override.class)
                .build());
    }

    private Optional<MethodSpec> findByIdMethod(TypeElement typeElement,
                                                DefaultNameContext nameContext, String repositoryFieldName, String classFieldName) {
        boolean containsNull = StringUtils.containsNull(nameContext.getVoPackageName());
        if (!containsNull) {
            return Optional.of(MethodSpec.methodBuilder("findById")
                    .addParameter(Long.class, "id")
                    .addModifiers(Modifier.PUBLIC)
                    .addCode(
                            CodeBlock.of("$T $L =  Optional.ofNullable($L.selectById(id));\n",
                                    ParameterizedTypeName.get(ClassName.get(Optional.class),
                                            ClassName.get(typeElement)), classFieldName, repositoryFieldName)
                    ).addCode(
                            CodeBlock.of("return new $T($L.orElseThrow(() -> new $T($T.NotFindError)));",
                                    ClassName.get(nameContext.getVoPackageName(), nameContext.getVoClassName()),
                                    classFieldName,
                                    BusinessException.class, CodeEnum.class)
                    )
                    .addAnnotation(Override.class)
                    .returns(ClassName.get(nameContext.getVoPackageName(), nameContext.getVoClassName()))
                    .build());
        }
        return Optional.empty();
    }

    private Optional<MethodSpec> findByPageMethod(TypeElement typeElement,
                                                  DefaultNameContext nameContext, String repositoryFieldName) {
        boolean containsNull = StringUtils.containsNull(nameContext.getDtoPackageName(),
                nameContext.getVoPackageName());
        if (!containsNull) {
            return Optional.of(MethodSpec.methodBuilder("findByPage")
                    .addParameter(ParameterizedTypeName.get(ClassName.get(PageRequestWrapper.class),
                                    ClassName.get(nameContext.getDtoPackageName(), nameContext.getDtoClassName())),
                            "query")
                    .addModifiers(Modifier.PUBLIC)
                    .addCode(
                            CodeBlock.of("$T page = new $T<>(query.getPage(), query.getPageSize());\n" +
                                            "page.setOptimizeCountSql(true);\n" +
                                            "page.setOrders($T.newArrayList($T.desc(\"create_at\")));\n",
                                    ParameterizedTypeName.get(
                                            ClassName.get(Page.class),
                                            ClassName.get(typeElement)
                                    ),
                                    Page.class,
                                    Lists.class,
                                    OrderItem.class
                            )
                    )
                    .addCode(
                            CodeBlock.of("final $T wrapper = new $T<>();\n",
                                    ParameterizedTypeName.get(ClassName.get(LambdaQueryWrapper.class), ClassName.get(typeElement)),
                                    LambdaQueryWrapper.class
                            )
                    )
                    .addCode(
                            CodeBlock.of(
                                    "final $T pageData = $L.selectPage(page, wrapper);\n",
                                    ParameterizedTypeName.get(
                                            ClassName.get(Page.class),
                                            ClassName.get(typeElement)
                                    ),
                                    repositoryFieldName
                            )
                    )
                    .addCode(
                            CodeBlock.of(
                                    "final $T data = new $T<>(pageData.getCurrent(), pageData.getSize(), pageData.getTotal());\n" +
                                            "data.setRecords(\n" +
                                            "       pageData.getRecords().stream()\n" +
                                            "               .map($T::new)\n" +
                                            "               .collect($T.toList())\n" +
                                            ");\n" +
                                            "return data;",
                                    ParameterizedTypeName.get(
                                            ClassName.get(Page.class),
                                            ClassName.get(nameContext.getVoPackageName(), nameContext.getVoClassName())
                                    ),
                                    Page.class,
                                    ClassName.get(nameContext.getVoPackageName(), nameContext.getVoClassName()),
                                    Collectors.class)
                    )
                    .addAnnotation(Override.class)
                    .returns(ParameterizedTypeName.get(ClassName.get(Page.class),
                            ClassName.get(nameContext.getVoPackageName(), nameContext.getVoClassName())))
                    .build());
        }
        return Optional.empty();
    }

}
