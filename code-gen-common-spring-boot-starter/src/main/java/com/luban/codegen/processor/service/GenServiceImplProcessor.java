package com.hp.codegen.processor.service;


import com.google.auto.service.AutoService;
import com.google.common.base.CaseFormat;
import com.hp.codegen.context.DefaultNameContext;
import com.hp.codegen.processor.AbstractCodeGenProcessor;
import com.hp.codegen.spi.CodeGenProcessor;
import com.hp.codegen.util.StringUtils;
import com.hp.jpa.EntityOperations;
import com.luban.common.core.domain.PageRequestWrapper;
import com.luban.common.core.enums.CodeEnum;
import com.luban.common.core.exception.BusinessException;
import com.querydsl.core.BooleanBuilder;
import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author gim 获取名称时可以先获取上下文再取，不用一个个的取，这样更方便
 */
@AutoService(value = CodeGenProcessor.class)
public class GenServiceImplProcessor extends AbstractCodeGenProcessor {

    public static final String IMPL_SUFFIX = "ServiceImpl";

    @Override
    protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        DefaultNameContext nameContext = getNameContext(typeElement);
        String className = typeElement.getSimpleName() + IMPL_SUFFIX;
        TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(className)
                .addSuperinterface(
                        ClassName.get(nameContext.getServicePackageName(), nameContext.getServiceClassName()))
                .addAnnotation(Transactional.class)
                .addAnnotation(Service.class)
                .addAnnotation(Slf4j.class)
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
        createMethod.ifPresent(m -> typeSpecBuilder.addMethod(m));

        Optional<MethodSpec> updateMethod = updateMethod(typeElement, nameContext, repositoryFieldName);
        updateMethod.ifPresent(m -> typeSpecBuilder.addMethod(m));

        Optional<MethodSpec> validMethod = validMethod(typeElement, repositoryFieldName);
        validMethod.ifPresent(m -> typeSpecBuilder.addMethod(m));

        Optional<MethodSpec> invalidMethod = invalidMethod(typeElement, repositoryFieldName);
        invalidMethod.ifPresent(m -> typeSpecBuilder.addMethod(m));

        Optional<MethodSpec> findByIdMethod = findByIdMethod(typeElement, nameContext, repositoryFieldName, classFieldName);
        findByIdMethod.ifPresent(m -> typeSpecBuilder.addMethod(m));

        Optional<MethodSpec> findByPageMethod = findByPageMethod(typeElement, nameContext, repositoryFieldName);
        findByPageMethod.ifPresent(m -> typeSpecBuilder.addMethod(m));

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
                    .addParameter(
                            ClassName.get(nameContext.getDtoPackageName(), nameContext.getDtoClassName()),
                            "creator")
                    .addModifiers(Modifier.PUBLIC)
                    .addCode(
                            CodeBlock.of(
                                    "Optional<$T> $L = $T.doCreate($L)\n.create(() -> $T.INSTANCE.dtoToEntity(creator))\n"
                                            + ".update(e -> e.init())\n"
                                            + ".execute();\n",
                                    typeElement, classFieldName, EntityOperations.class, repositoryFieldName,
                                    ClassName.get(nameContext.getMapperPackageName(),
                                            nameContext.getMapperClassName()))
                    )
                    .addCode(
                            CodeBlock.of("return $L.isPresent() ? $L.get().getId() : 0;", classFieldName,
                                    classFieldName)
                    )
                    .addJavadoc("createImpl")
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
                    .addParameter(
                            ClassName.get(nameContext.getDtoPackageName(), nameContext.getDtoClassName()),
                            "updater")
                    .addModifiers(Modifier.PUBLIC)
                    .addCode(
                            CodeBlock.of("$T.doUpdate($L)\n.loadById(updater.getId())\n"
                                            + ".update(e -> updater.update$L(e))\n"
                                            + ".execute();",
                                    EntityOperations.class, repositoryFieldName, typeElement.getSimpleName())
                    )
                    .addJavadoc("update")
                    .addAnnotation(Override.class)
                    .build());
        }
        return Optional.empty();
    }

    private Optional<MethodSpec> validMethod(TypeElement typeElement, String repositoryFieldName) {
        return Optional.of(MethodSpec.methodBuilder("valid" + typeElement.getSimpleName())
                .addParameter(Long.class, "id")
                .addModifiers(Modifier.PUBLIC)
                .addCode(
                        CodeBlock.of("$T.doUpdate($L)\n.loadById(id)\n"
                                        + ".update(e -> e.valid())\n"
                                        + ".execute();",
                                EntityOperations.class, repositoryFieldName)
                )
                .addJavadoc("valid")
                .addAnnotation(Override.class)
                .build());
    }

    private Optional<MethodSpec> invalidMethod(TypeElement typeElement, String repositoryFieldName) {
        return Optional.of(MethodSpec.methodBuilder("invalid" + typeElement.getSimpleName())
                .addParameter(Long.class, "id")
                .addModifiers(Modifier.PUBLIC)
                .addCode(
                        CodeBlock.of("$T.doUpdate($L)\n.loadById(id)\n"
                                        + ".update(e -> e.invalid())\n"
                                        + ".execute();",
                                EntityOperations.class, repositoryFieldName)
                )
                .addJavadoc("invalid")
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
                            CodeBlock.of("$T $L =  $L.findById(id);\n",
                                    ParameterizedTypeName.get(ClassName.get(Optional.class),
                                            ClassName.get(typeElement)), classFieldName, repositoryFieldName)
                    ).addCode(
                            CodeBlock.of("return new $T($L.orElseThrow(() -> new $T($T.NotFindError)));",
                                    ClassName.get(nameContext.getVoPackageName(), nameContext.getVoClassName()),
                                    classFieldName,
                                    BusinessException.class, CodeEnum.class)
                    )
                    .addJavadoc("findById")
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
                            CodeBlock.of("$T booleanBuilder = new $T();\n", BooleanBuilder.class,
                                    BooleanBuilder.class)
                    )
                    .addCode(
                            CodeBlock.of("$T<$T> page = $L.findAll(booleanBuilder,\n"
                                            + "        $T.of(query.getPage() - 1, query.getPageSize(), $T.by(\n"
                                            + "            $T.DESC, \"createdAt\")));\n", Page.class, typeElement,
                                    repositoryFieldName,
                                    PageRequest.class, Sort.class, Direction.class)
                    )
                    .addCode(
                            CodeBlock.of(
                                    "return new $T<>(page.getContent().stream().map(entity -> new $T(entity))\n"
                                            + "        .collect($T.toList()), page.getPageable(), page.getTotalElements());",
                                    PageImpl.class,
                                    ClassName.get(nameContext.getVoPackageName(), nameContext.getVoClassName()),
                                    Collectors.class)
                    )
                    .addJavadoc("findByPage")
                    .addAnnotation(Override.class)
                    .returns(ParameterizedTypeName.get(ClassName.get(Page.class),
                            ClassName.get(nameContext.getVoPackageName(), nameContext.getVoClassName())))
                    .build());
        }
        return Optional.empty();
    }

}
