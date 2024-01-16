package com.luban.codegen.processor.service.jpa;


import cn.hutool.core.collection.CollUtil;
import com.google.auto.service.AutoService;
import com.google.common.base.CaseFormat;
import com.luban.codegen.constant.Orm;
import com.luban.codegen.context.DefaultNameContext;
import com.luban.codegen.processor.AbstractCodeGenProcessor;
import com.luban.codegen.processor.event.GenEventProcessor;
import com.luban.codegen.processor.service.GenServiceImpl;
import com.luban.codegen.spi.CodeGenProcessor;
import com.luban.codegen.util.StringUtils;
import com.luban.common.base.enums.CodeEnum;
import com.luban.common.base.exception.BusinessException;
import com.luban.common.base.model.PageRequestWrapper;
import com.luban.common.base.model.PageResponse;
import com.luban.jpa.EntityOperations;
import com.querydsl.core.BooleanBuilder;
import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author hp
 */
@AutoService(value = CodeGenProcessor.class)
public class GenServiceImplProcessor extends AbstractCodeGenProcessor {

    public static final String IMPL_SUFFIX = "ServiceImpl";

    @Override
    public boolean supportedOrm(Orm orm) {
        return Orm.SPRING_DATA_JPA.equals(orm);
    }

    @Override
    protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        final DefaultNameContext nameContext = getNameContext();
        final String className = typeElement.getSimpleName() + IMPL_SUFFIX;
        final TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(className)
                .addSuperinterface(ClassName.get(nameContext.getServicePackageName(), nameContext.getServiceClassName()))
                .addAnnotation(Slf4j.class)
                .addAnnotation(Service.class)
                .addAnnotation(RequiredArgsConstructor.class)
                .addAnnotation(AnnotationSpec.builder(Transactional.class).addMember("rollbackFor", "$L", "Exception.class").build())
                .addModifiers(Modifier.PUBLIC);
        if (StringUtils.containsNull(nameContext.getRepositoryPackageName())) {
            return;
        }
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

        typeSpecBuilder.addField(repositoryField);
        typeSpecBuilder.addField(applicationEventPublisherField);

        createUsingCommandMethod(typeElement, nameContext, repositoryFieldName).ifPresent(typeSpecBuilder::addMethod);
        updateUsingCommandMethod(typeElement, nameContext, repositoryFieldName).ifPresent(typeSpecBuilder::addMethod);
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

    @Deprecated(forRemoval = true)
    private Optional<MethodSpec> createMethod(
            TypeElement typeElement,
            DefaultNameContext nameContext,
            String repositoryFieldName
    ) {
        if (StringUtils.containsNull(nameContext.getDtoPackageName(), nameContext.getMapperPackageName())) {
            return Optional.empty();
        }
        return Optional.of(
                MethodSpec.methodBuilder("create" + typeElement.getSimpleName())
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
                                        ClassName.get(EntityOperations.class),
                                        repositoryFieldName,
                                        ClassName.get(nameContext.getMapperPackageName(), nameContext.getMapperClassName()),
                                        typeElement.getSimpleName(),
                                        typeElement.getSimpleName(),
                                        ClassName.get(BusinessException.class),
                                        ClassName.get(CodeEnum.class)
                                )
                        )
                        .addAnnotation(Override.class)
                        .returns(Long.class)
                        .build()
        );
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
                                            com.luban.mybatisplus.EntityOperations.class,
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
        return Optional.of(
                MethodSpec.methodBuilder("update" + typeElement.getSimpleName())
                        .addParameter(ClassName.get(nameContext.getDtoPackageName(), nameContext.getDtoClassName()), "updater")
                        .addModifiers(Modifier.PUBLIC)
                        .addCode(
                                CodeBlock.of(
                                        "$T.doUpdate($L)\n.loadById(updater.getId())\n"
                                                + ".update(updater::update$L)\n"
                                                + ".execute();",
                                        ClassName.get(EntityOperations.class),
                                        repositoryFieldName,
                                        typeElement.getSimpleName()
                                )
                        )
                        .addAnnotation(Override.class)
                        .build()
        );
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
                                            com.luban.mybatisplus.EntityOperations.class,
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
        return Optional.of(
                MethodSpec.methodBuilder("enable" + typeElement.getSimpleName())
                        .addParameter(Long.class, "id")
                        .addModifiers(Modifier.PUBLIC)
                        .addCode(
                                CodeBlock.of(
                                        "$T.doUpdate($L)\n.loadById(id)\n"
                                                + ".update($L::enable)\n"
                                                + ".execute();",
                                        ClassName.get(EntityOperations.class),
                                        repositoryFieldName,
                                        typeElement.getSimpleName()
                                )
                        )
                        .addAnnotation(Override.class)
                        .build()
        );
    }

    private Optional<MethodSpec> disableMethod(
            TypeElement typeElement,
            String repositoryFieldName
    ) {
        return Optional.of(
                MethodSpec.methodBuilder("disable" + typeElement.getSimpleName())
                        .addParameter(Long.class, "id")
                        .addModifiers(Modifier.PUBLIC)
                        .addCode(
                                CodeBlock.of(
                                        "$T.doUpdate($L)\n.loadById(id)\n"
                                                + ".update($L::disable)\n"
                                                + ".execute();",
                                        ClassName.get(EntityOperations.class),
                                        repositoryFieldName,
                                        typeElement.getSimpleName()
                                )
                        )
                        .addAnnotation(Override.class)
                        .build()
        );
    }

    private Optional<MethodSpec> findByIdMethod(
            TypeElement typeElement,
            String repositoryFieldName
    ) {
        return Optional.of(
                MethodSpec.methodBuilder("findById")
                        .addParameter(Long.class, "id")
                        .addModifiers(Modifier.PUBLIC)
                        .addCode(
                                CodeBlock.of(
                                        "return $L.findById(id).orElseThrow(() -> new $T($T.NotFindError));",
                                        repositoryFieldName,
                                        ClassName.get(BusinessException.class),
                                        ClassName.get(CodeEnum.class)
                                )
                        )
                        .addAnnotation(Override.class)
                        .returns(ClassName.get(typeElement))
                        .build()
        );
    }

    private Optional<MethodSpec> findAllByIdMethod(
            TypeElement typeElement,
            String repositoryFieldName
    ) {
        return Optional.of(
                MethodSpec.methodBuilder("findAllById")
                        .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(ClassName.get(Collection.class), ClassName.get(Long.class)), "ids").build())
                        .addModifiers(Modifier.PUBLIC)
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
                                CodeBlock.of(
                                        "final $T list =  $L.findAllById(ids);\n",
                                        ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get(typeElement)),
                                        repositoryFieldName
                                )
                        )
                        .addCode(
                                CodeBlock.of(
                                        "if ($T.isEmpty(list)) { \n" +
                                                "   return $T.emptyList();\n" +
                                                "}\n",
                                        ClassName.get(CollUtil.class),
                                        ClassName.get(Collections.class)
                                )
                        )
                        .addCode(
                                CodeBlock.of("return list;")
                        )
                        .addAnnotation(Override.class)
                        .returns(ParameterizedTypeName.get(ClassName.get(List.class), ClassName.get(typeElement)))
                        .build()
        );
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
                                "pageWrapper"
                        )
                        .addModifiers(Modifier.PUBLIC)
                        .addCode(
                                CodeBlock.of("final $T booleanBuilder = new $T();\n", ClassName.get(BooleanBuilder.class), ClassName.get(BooleanBuilder.class))
                        )
                        .addCode(
                                CodeBlock.of("final $T page = $L.findAll(booleanBuilder, $T.of(pageWrapper.getPage() - 1, pageWrapper.getPageSize(), $T.by($T.DESC, \"id\")));\n",
                                        ParameterizedTypeName.get(ClassName.get(Page.class), ClassName.get(typeElement)),
                                        repositoryFieldName,
                                        ClassName.get(PageRequest.class),
                                        ClassName.get(Sort.class),
                                        ClassName.get(Direction.class)
                                )
                        )
                        .addCode(
                                CodeBlock.of(
                                        "return $T.of(\n" +
                                                "page.getContent()\n" +
                                                "   .stream()\n" +
                                                "   .map($T.INSTANCE::entityToCustomPageResponse)\n" +
                                                "   .collect($T.toList()),\n" +
                                                "page.getTotalElements(),\n" +
                                                "pageWrapper \n" +
                                                ");\n",
                                        ClassName.get(PageResponse.class),
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
