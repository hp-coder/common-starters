package com.luban.codegen.processor.service.jpa;


import cn.hutool.core.collection.CollUtil;
import com.google.auto.service.AutoService;
import com.google.common.base.CaseFormat;
import com.luban.codegen.context.DefaultNameContext;
import com.luban.codegen.processor.AbstractCodeGenProcessor;
import com.luban.codegen.processor.service.GenServiceImpl;
import com.luban.codegen.spi.CodeGenProcessor;
import com.luban.codegen.util.StringUtils;
import com.luban.common.base.enums.CodeEnum;
import com.luban.common.base.exception.BusinessException;
import com.luban.common.base.model.PageRequestWrapper;
import com.luban.jpa.EntityOperations;
import com.querydsl.core.BooleanBuilder;
import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

/**
 * @author hp
 */
@AutoService(value = CodeGenProcessor.class)
public class GenServiceImplProcessor extends AbstractCodeGenProcessor {

    public static final String IMPL_SUFFIX = "ServiceImpl";

    @Override
    protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        final DefaultNameContext nameContext = getNameContext();
        final String className = typeElement.getSimpleName() + IMPL_SUFFIX;
        final TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(className)
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
        final String repositoryFieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL,
                nameContext.getRepositoryClassName());

        final FieldSpec repositoryField = FieldSpec
                .builder(ClassName.get(nameContext.getRepositoryPackageName(),
                        nameContext.getRepositoryClassName()), repositoryFieldName)
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build();
        typeSpecBuilder.addField(repositoryField);

        createMethod(typeElement, nameContext, repositoryFieldName).ifPresent(typeSpecBuilder::addMethod);
        updateMethod(typeElement, nameContext, repositoryFieldName).ifPresent(typeSpecBuilder::addMethod);
        enableMethod(typeElement, repositoryFieldName).ifPresent(typeSpecBuilder::addMethod);
        disableMethod(typeElement, repositoryFieldName).ifPresent(typeSpecBuilder::addMethod);
        findByIdMethod(typeElement, nameContext, repositoryFieldName).ifPresent(typeSpecBuilder::addMethod);
        findAllByIdMethod(typeElement, nameContext, repositoryFieldName).ifPresent(typeSpecBuilder::addMethod);
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
        if (!StringUtils.containsNull(nameContext.getDtoClassName(), nameContext.getMapperPackageName())) {
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
        return Optional.empty();
    }

    private Optional<MethodSpec> updateMethod(
            TypeElement typeElement,
            DefaultNameContext nameContext,
            String repositoryFieldName
    ) {
        if (!StringUtils.containsNull(nameContext.getDtoPackageName())) {
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
        return Optional.empty();
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
                                                + ".update($L::valid)\n"
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
                                                + ".update($L::invalid)\n"
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
            DefaultNameContext nameContext,
            String repositoryFieldName
    ) {
        if (!StringUtils.containsNull(nameContext.getVoPackageName())) {
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
        return Optional.empty();
    }

    private Optional<MethodSpec> findAllByIdMethod(
            TypeElement typeElement,
            DefaultNameContext nameContext,
            String repositoryFieldName
    ) {
        if (!StringUtils.containsNull(nameContext.getVoPackageName())) {
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
        return Optional.empty();
    }

    private Optional<MethodSpec> findByPageMethod(
            TypeElement typeElement,
            DefaultNameContext nameContext,
            String repositoryFieldName
    ) {
        if (!StringUtils.containsNull(nameContext.getDtoPackageName(), nameContext.getVoPackageName())) {
            return Optional.of(
                    MethodSpec.methodBuilder("findByPage")
                            .addParameter(
                                    ParameterizedTypeName.get(
                                            ClassName.get(PageRequestWrapper.class),
                                            ClassName.get(nameContext.getDtoPackageName(), nameContext.getDtoClassName())
                                    ),
                                    "query"
                            )
                            .addModifiers(Modifier.PUBLIC)
                            .addCode(
                                    CodeBlock.of("$T booleanBuilder = new $T();\n", ClassName.get(BooleanBuilder.class), ClassName.get(BooleanBuilder.class))
                            )
                            .addCode(
                                    CodeBlock.of("$T page = $L.findAll(booleanBuilder, $T.of(query.getPage() - 1, query.getPageSize(), $T.by($T.DESC, \"createdAt\")));\n",
                                            ParameterizedTypeName.get(ClassName.get(Page.class), ClassName.get(typeElement)),
                                            repositoryFieldName,
                                            ClassName.get(PageRequest.class),
                                            ClassName.get(Sort.class),
                                            ClassName.get(Direction.class)
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
