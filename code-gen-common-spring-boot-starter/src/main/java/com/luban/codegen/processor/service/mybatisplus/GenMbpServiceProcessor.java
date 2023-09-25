package com.luban.codegen.processor.service.mybatisplus;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.google.auto.service.AutoService;
import com.luban.codegen.constant.Orm;
import com.luban.codegen.context.DefaultNameContext;
import com.luban.codegen.processor.AbstractCodeGenProcessor;
import com.luban.codegen.processor.service.GenService;
import com.luban.codegen.spi.CodeGenProcessor;
import com.luban.codegen.util.StringUtils;
import com.luban.common.base.model.PageRequestWrapper;
import com.squareup.javapoet.*;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * @author hp
 */
@AutoService(value = CodeGenProcessor.class)
public class GenMbpServiceProcessor extends AbstractCodeGenProcessor {

    public static final String SERVICE_SUFFIX = "Service";

    public static final String SERVICE_PREFIX = "I";

    @Override
    public boolean supportedOrm(Orm orm) {
        return Objects.equals(orm, Orm.MYBATIS_PLUS);
    }

    @Override
    protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        String className = SERVICE_PREFIX + typeElement.getSimpleName() + SERVICE_SUFFIX;
        TypeSpec.Builder typeSpecBuilder = TypeSpec.interfaceBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(IService.class), ClassName.get(typeElement)));

        DefaultNameContext nameContext = getNameContext();

        createMethod(typeElement, nameContext).ifPresent(typeSpecBuilder::addMethod);
        createUsingCommandMethod(typeElement, nameContext).ifPresent(typeSpecBuilder::addMethod);
        updateMethod(typeElement, nameContext).ifPresent(typeSpecBuilder::addMethod);
        updateUsingCommandMethod(typeElement, nameContext).ifPresent(typeSpecBuilder::addMethod);
        enableMethod(typeElement).ifPresent(typeSpecBuilder::addMethod);
        disableMethod(typeElement).ifPresent(typeSpecBuilder::addMethod);
        findByIdMethod(typeElement).ifPresent(typeSpecBuilder::addMethod);
        findAllByIdMethod(typeElement).ifPresent(typeSpecBuilder::addMethod);
        findByPageMethod(nameContext).ifPresent(typeSpecBuilder::addMethod);

        generateJavaSourceFile(generatePackage(typeElement), generatePath(typeElement), typeSpecBuilder);
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenService.class;
    }

    @Override
    public String generatePackage(TypeElement typeElement) {
        return typeElement.getAnnotation(GenService.class).pkgName();
    }

    @Override
    public String generatePath(TypeElement typeElement) {
        return typeElement.getAnnotation(GenService.class).sourcePath();
    }

    private Optional<MethodSpec> createMethod(TypeElement typeElement, DefaultNameContext nameContext) {
        if (StringUtils.containsNull(nameContext.getDtoPackageName(), nameContext.getDtoClassName())) {
            return Optional.empty();
        }
        return Optional.of(
                MethodSpec.methodBuilder("create" + typeElement.getSimpleName())
                        .addParameter(ClassName.get(nameContext.getDtoPackageName(), nameContext.getDtoClassName()), "creator")
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT).returns(Long.class)
                        .build()
        );
    }

    private Optional<MethodSpec> createUsingCommandMethod(TypeElement typeElement, DefaultNameContext nameContext) {
        if (StringUtils.containsNull(nameContext.getCreateCommandPackageName(), nameContext.getCreateCommandClassName())) {
            return Optional.empty();
        }
        return Optional.of(
                MethodSpec.methodBuilder("create" + typeElement.getSimpleName())
                        .addParameter(ClassName.get(nameContext.getCreateCommandPackageName(), nameContext.getCreateCommandClassName()), "command")
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT).returns(Long.class)
                        .build()
        );
    }

    private Optional<MethodSpec> updateMethod(TypeElement typeElement, DefaultNameContext nameContext) {
        if (StringUtils.containsNull(nameContext.getDtoPackageName(), nameContext.getDtoClassName())) {
            return Optional.empty();
        }
        return Optional.of(
                MethodSpec.methodBuilder("update" + typeElement.getSimpleName())
                        .addParameter(ClassName.get(nameContext.getDtoPackageName(), nameContext.getDtoClassName()), "updater")
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .build()
        );
    }

    private Optional<MethodSpec> updateUsingCommandMethod(TypeElement typeElement, DefaultNameContext nameContext) {
        if (StringUtils.containsNull(nameContext.getUpdateCommandPackageName(), nameContext.getUpdateCommandClassName())) {
            return Optional.empty();
        }
        return Optional.of(
                MethodSpec.methodBuilder("update" + typeElement.getSimpleName())
                        .addParameter(ClassName.get(nameContext.getUpdateCommandPackageName(), nameContext.getUpdateCommandClassName()), "command")
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .build()
        );
    }

    private Optional<MethodSpec> enableMethod(TypeElement typeElement) {
        return Optional.of(
                MethodSpec.methodBuilder("enable" + typeElement.getSimpleName())
                        .addParameter(Long.class, "id")
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .build()
        );
    }

    private Optional<MethodSpec> disableMethod(TypeElement typeElement) {
        return Optional.of(
                MethodSpec.methodBuilder("disable" + typeElement.getSimpleName())
                        .addParameter(Long.class, "id")
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .build()
        );
    }

    private Optional<MethodSpec> findByIdMethod(TypeElement typeElement) {
        return Optional.of(
                MethodSpec.methodBuilder("findById")
                        .addParameter(Long.class, "id")
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .returns(ClassName.get(typeElement))
                        .build()
        );
    }

    private Optional<MethodSpec> findAllByIdMethod(TypeElement typeElement) {
        return Optional.of(
                MethodSpec.methodBuilder("findAllById")
                        .addParameter(ParameterSpec.builder(ParameterizedTypeName.get(ClassName.get(Collection.class), ClassName.get(Long.class)), "ids").build())
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .returns(ParameterizedTypeName.get(ClassName.get(Collection.class), ClassName.get(typeElement)))
                        .build()
        );
    }

    private Optional<MethodSpec> findByPageMethod(DefaultNameContext nameContext) {
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
                                "query"
                        )
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .returns(ParameterizedTypeName.get(ClassName.get(Page.class), ClassName.get(nameContext.getPageResponsePackageName(), nameContext.getPageResponseClassName())))
                        .build()
        );
    }

}
