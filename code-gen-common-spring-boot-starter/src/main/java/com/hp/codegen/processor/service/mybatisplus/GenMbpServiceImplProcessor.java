package com.hp.codegen.processor.service.mybatisplus;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.auto.service.AutoService;
import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.hp.codegen.constant.Orm;
import com.hp.codegen.context.DefaultNameContext;
import com.hp.codegen.processor.AbstractCodeGenProcessor;
import com.hp.codegen.processor.event.GenEventProcessor;
import com.hp.codegen.processor.service.GenServiceImpl;
import com.hp.codegen.spi.CodeGenProcessor;
import com.hp.codegen.util.StringUtils;
import com.hp.common.base.enums.CodeEnum;
import com.hp.common.base.exception.BusinessException;
import com.hp.common.base.model.PageRequestWrapper;
import com.hp.common.base.model.PageResponse;
import com.hp.mybatisplus.EntityOperations;
import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author hp
 */
@AutoService(value = CodeGenProcessor.class)
public class GenMbpServiceImplProcessor extends AbstractCodeGenProcessor {

    public static final String IMPL_SUFFIX = "ServiceImpl";

    public static String getServiceImplName(TypeElement typeElement) {
        return typeElement.getSimpleName() + IMPL_SUFFIX;
    }

    @Override
    public boolean supportedOrm(Orm orm) {
        return Objects.equals(orm, Orm.MYBATIS_PLUS);
    }

    @Override
    protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        if (StringUtils.containsNull(nameContext.getRepositoryPackageName())) {
            return;
        }
        final DefaultNameContext nameContext = getNameContext();
        final TypeSpec.Builder builder = TypeSpec.classBuilder(getServiceImplName(typeElement))
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

        final String repositoryFieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL,
                nameContext.getRepositoryClassName());

        final FieldSpec repositoryField = FieldSpec
                .builder(ClassName.get(nameContext.getRepositoryPackageName(),
                        nameContext.getRepositoryClassName()), repositoryFieldName)
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build();

        final FieldSpec applicationEventPublisherField = FieldSpec
                .builder(ClassName.get(ApplicationEventPublisher.class), "eventPublisher")
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build();

        builder.addField(repositoryField);
        builder.addField(applicationEventPublisherField);

        createUsingCommandMethod(typeElement, nameContext, repositoryFieldName).ifPresent(builder::addMethod);
        updateMethod(typeElement, nameContext, repositoryFieldName).ifPresent(builder::addMethod);
        enableMethod(typeElement, repositoryFieldName).ifPresent(builder::addMethod);
        disableMethod(typeElement, repositoryFieldName).ifPresent(builder::addMethod);
        findByIdMethod(typeElement, repositoryFieldName).ifPresent(builder::addMethod);
        findAllByIdMethod(typeElement, repositoryFieldName).ifPresent(builder::addMethod);
        findByPageMethod(typeElement, nameContext, repositoryFieldName).ifPresent(builder::addMethod);

        generateJavaSourceFile(generatePackage(typeElement), generatePath(typeElement), builder);
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

    @Deprecated(forRemoval = true)
    private Optional<MethodSpec> createMethod(
            TypeElement typeElement,
            DefaultNameContext nameContext,
            String repositoryFieldName
    ) {
        if (StringUtils.containsNull(nameContext.getDtoPackageName(), nameContext.getMapperPackageName())) {
            return Optional.empty();
        }
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

    private Optional<MethodSpec> createUsingCommandMethod(
            TypeElement typeElement,
            DefaultNameContext nameContext,
            String repositoryFieldName
    ) {
        if (StringUtils.containsNull(nameContext.getCreateCommandPackageName())) {
            return Optional.empty();
        }
        final MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("create" + typeElement.getSimpleName())
                .addParameter(ClassName.get(nameContext.getCreateCommandPackageName(), nameContext.getCreateCommandClassName()), "command")
                .addModifiers(Modifier.PUBLIC)
                .addCode(
                        CodeBlock.of(
                                "final $T context = $T.create(command);\n",
                                ClassName.get(nameContext.getCreateContextPackageName(), nameContext.getCreateContextClassName()),
                                ClassName.get(nameContext.getCreateContextPackageName(), nameContext.getCreateContextClassName())
                        )
                )
                .addAnnotation(Override.class)
                .returns(Long.class);
        if (StringUtils.containsNull(nameContext.getEventPackageName(), nameContext.getEventClassName())) {
            return Optional.of(
                    methodBuilder.addCode(
                                    CodeBlock.of(
                                            "return $T.doCreate($L)\n" +
                                                    ".create(() -> $T.$L(context))\n" +
                                                    ".update(entity -> {})\n" +
                                                    ".execute()\n" +
                                                    ".map($L::getId)\n" +
                                                    ".orElseThrow(() -> new $T($T.SaveError));",
                                            EntityOperations.class,
                                            repositoryFieldName,
                                            ClassName.get(typeElement),
                                            "create" + typeElement.getSimpleName(),
                                            typeElement.getSimpleName(),
                                            ClassName.get(BusinessException.class),
                                            ClassName.get(CodeEnum.class)
                                    )
                            )
                            .build()
            );
        }
        return Optional.of(
                methodBuilder
                        .addCode(
                                CodeBlock.of(
                                        "return $T.doCreate($L)\n" +
                                                ".create(() -> $T.$L(context))\n" +
                                                ".update(entity -> {})\n" +
                                                ".successHook(entity -> eventPublisher.publishEvent(new $T(context)))\n" +
                                                ".execute()\n" +
                                                ".map($L::getId)\n" +
                                                ".orElseThrow(() -> new $T($T.SaveError));",
                                        EntityOperations.class,
                                        repositoryFieldName,
                                        ClassName.get(typeElement),
                                        "create" + typeElement.getSimpleName(),
                                        ClassName.get(nameContext.getEventPackageName(), nameContext.getEventClassName(), GenEventProcessor.getCreatedEventName(typeElement)),
                                        typeElement.getSimpleName(),
                                        ClassName.get(BusinessException.class),
                                        ClassName.get(CodeEnum.class)
                                )
                        )
                        .build()
        );
    }

    @Deprecated(forRemoval = true)
    private Optional<MethodSpec> updateMethod(
            TypeElement typeElement,
            DefaultNameContext nameContext,
            String repositoryFieldName
    ) {
        if (StringUtils.containsNull(nameContext.getDtoPackageName(), nameContext.getMapperPackageName())) {
            return Optional.empty();
        }
        return Optional.of(MethodSpec.methodBuilder("update" + typeElement.getSimpleName())
                .addParameter(ClassName.get(nameContext.getDtoPackageName(), nameContext.getDtoClassName()), "updater")
                .addModifiers(Modifier.PUBLIC)
                .addCode(
                        CodeBlock.of(
                                "$T.doUpdate($L)\n" +
                                        ".loadById(updater.getId())\n" +
                                        ".update(updater::update$L)\n" +
                                        ".execute();",
                                EntityOperations.class,
                                repositoryFieldName,
                                typeElement.getSimpleName()
                        )
                )
                .addAnnotation(Override.class)
                .build());
    }

    private Optional<MethodSpec> updateUsingCommandMethod(
            TypeElement typeElement,
            DefaultNameContext nameContext,
            String repositoryFieldName
    ) {
        if (StringUtils.containsNull(nameContext.getUpdateCommandPackageName())) {
            return Optional.empty();
        }
        final MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("update" + typeElement.getSimpleName())
                .addParameter(ClassName.get(nameContext.getUpdateCommandPackageName(), nameContext.getUpdateCommandClassName()), "command")
                .addCode(
                        CodeBlock.of(
                                "final $T context = $T.create(command);",
                                ClassName.get(nameContext.getUpdateContextPackageName(), nameContext.getUpdateContextClassName()),
                                ClassName.get(nameContext.getUpdateContextPackageName(), nameContext.getUpdateContextClassName())
                        )
                )
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class);

        if (StringUtils.containsNull(nameContext.getEventPackageName(), nameContext.getEventClassName())) {
            return Optional.of(
                    methodBuilder
                            .addCode(
                                    CodeBlock.of(
                                            "$T.doUpdate($L)\n.loadById(command.getId())\n" +
                                                    ".update(entity -> entity.update$L(context))\n" +
                                                    ".execute();",
                                            EntityOperations.class,
                                            repositoryFieldName,
                                            typeElement.getSimpleName()
                                    )
                            )
                            .build()
            );
        }
        return Optional.of(
                methodBuilder
                        .addCode(
                                CodeBlock.of(
                                        "$T.doUpdate($L)\n.loadById(command.getId())\n" +
                                                ".update(entity -> entity.update$L(context))\n" +
                                                ".successHook(entity -> eventPublisher.publishEvent(new $T(context)))\n" +
                                                ".execute();",
                                        EntityOperations.class,
                                        repositoryFieldName,
                                        typeElement.getSimpleName(),
                                        ClassName.get(nameContext.getEventPackageName(), nameContext.getEventClassName(), GenEventProcessor.getUpdatedEventName(typeElement))
                                )
                        )
                        .build()
        );
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
                                        + ".update($L::enable)\n"
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
                                        + ".update($L::disable)\n"
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
        if (StringUtils.containsNull(nameContext.getPageRequestPackageName(), nameContext.getPageResponsePackageName())) {
            return Optional.empty();
        }
        return Optional.of(
                MethodSpec.methodBuilder("findByPage")
                        .addParameter(
                                ParameterizedTypeName.get(
                                        ClassName.get(PageRequestWrapper.class),
                                        ClassName.get(nameContext.getPageRequestPackageName(), nameContext.getPageRequestClassName())
                                ),
                                "requestWrapper"
                        )
                        .addModifiers(Modifier.PUBLIC)
                        .addCode(
                                CodeBlock.of(
                                        "$T page = new $T<>(requestWrapper.getPage(), requestWrapper.getSize());\n" +
                                                "page.setOrders($T.newArrayList($T.desc(\"id\")));\n",
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
                                CodeBlock.of(
                                        " return $T.of(\n" +
                                                "page.getRecords().stream()\n" +
                                                ".map($T.INSTANCE::entityToCustomPageResponse)\n" +
                                                ".collect($T.toList()),\n" +
                                                "page.getTotal(),\n"+
                                                "requestWrapper\n"+
                                                ");\n",
                                        ClassName.get(nameContext.getMapperPackageName(), nameContext.getMapperClassName()),
                                        ClassName.get(Collectors.class)
                                )
                        )
                        .addAnnotation(Override.class)
                        .returns(ParameterizedTypeName.get(ClassName.get(PageResponse.class), ClassName.get(nameContext.getPageResponsePackageName(), nameContext.getPageResponseClassName())))
                        .build()
        );
    }
}
