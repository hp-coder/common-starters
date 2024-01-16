package com.luban.codegen.processor.api;

import com.google.auto.service.AutoService;
import com.google.common.base.CaseFormat;
import com.luban.codegen.context.DefaultNameContext;
import com.luban.codegen.processor.AbstractCodeGenProcessor;
import com.luban.codegen.spi.CodeGenProcessor;
import com.luban.codegen.util.StringUtils;
import com.luban.common.base.model.PageResponse;
import com.luban.common.base.model.Returns;
import com.squareup.javapoet.*;
import com.squareup.javapoet.TypeSpec.Builder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.Optional;

/**
 * @author hp
 */
@AutoService(value = CodeGenProcessor.class)
public class GenFeignProcessor extends AbstractCodeGenProcessor {

    public static String FEIGN_SUFFIX = "FeignService";

    @Override
    protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        final String classFieldName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, typeElement.getSimpleName().toString());
        final GenFeign feign = typeElement.getAnnotation(GenFeign.class);
        final Builder builder = TypeSpec.interfaceBuilder(nameContext.getFeignClassName())
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec
                        .builder(FeignClient.class)
                        .addMember("value", "$S", feign.serverName())
                        .addMember("contextId", "$S", classFieldName + "Client")
                        .addMember("path", "$S", classFieldName + "/v1")
                        .build());

        createMethod(typeElement, nameContext).ifPresent(builder::addMethod);
        updateMethod(typeElement, nameContext).ifPresent(builder::addMethod);
        enableMethod(typeElement).ifPresent(builder::addMethod);
        disableMethod(typeElement).ifPresent(builder::addMethod);
        findById(nameContext).ifPresent(builder::addMethod);
        findByPage(nameContext).ifPresent(builder::addMethod);

        generateJavaSourceFile(generatePackage(typeElement), generatePath(typeElement), builder);
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenFeign.class;
    }

    @Override
    public String generatePath(TypeElement typeElement) {
        return typeElement.getAnnotation(GenFeign.class).sourcePath();
    }

    @Override
    public String generatePackage(TypeElement typeElement) {
        return typeElement.getAnnotation(GenFeign.class).pkgName();
    }

    private Optional<MethodSpec> createMethod(TypeElement typeElement, DefaultNameContext nameContext) {
        if (StringUtils.containsNull(nameContext.getCreateRequestPackageName(), nameContext.getCreateCommandPackageName(), nameContext.getMapperPackageName())) {
            return Optional.empty();
        }
        return Optional.of(MethodSpec.methodBuilder("create" + typeElement.getSimpleName())
                .addParameter(
                        ParameterSpec.builder(ClassName.get(nameContext.getCreateRequestPackageName(), nameContext.getCreateRequestClassName()), "request")
                                .addAnnotation(RequestBody.class).build()
                )
                .addAnnotation(
                        AnnotationSpec.builder(PostMapping.class)
                                .addMember("value", "$S", "_create")
                                .build()
                )
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(ParameterizedTypeName.get(ClassName.get(Returns.class), ClassName.get(Long.class))).build());
    }

    private Optional<MethodSpec> updateMethod(TypeElement typeElement, DefaultNameContext nameContext) {
        if (StringUtils.containsNull(nameContext.getCreateRequestPackageName(), nameContext.getCreateCommandPackageName(), nameContext.getMapperPackageName())) {
            return Optional.empty();
        }
        return Optional.of(MethodSpec.methodBuilder("update" + typeElement.getSimpleName())
                .addParameter(
                        ParameterSpec.builder(ClassName.get(nameContext.getUpdateRequestPackageName(), nameContext.getUpdateRequestClassName()), "request")
                                .addAnnotation(RequestBody.class)
                                .build()
                )
                .addAnnotation(
                        AnnotationSpec.builder(PostMapping.class)
                                .addMember("value", "$S", "_update")
                                .build()
                )
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(ParameterizedTypeName.get(ClassName.get(Returns.class), ClassName.get(Void.class)))
                .build());
    }

    private Optional<MethodSpec> enableMethod(TypeElement typeElement) {
        return Optional.of(MethodSpec.methodBuilder("enable" + typeElement.getSimpleName())
                .addParameter(
                        ParameterSpec.builder(Long.class, "id")
                                .addAnnotation(
                                        AnnotationSpec.builder(PathVariable.class)
                                                .addMember("value", "$S", "id")
                                                .build()
                                )
                                .build()
                )
                .addAnnotation(
                        AnnotationSpec.builder(PostMapping.class)
                                .addMember("value", "$S", "enable/{id}")
                                .build()
                )
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(ParameterizedTypeName.get(ClassName.get(Returns.class), ClassName.get(Void.class)))
                .build());
    }

    private Optional<MethodSpec> disableMethod(TypeElement typeElement) {
        return Optional.of(MethodSpec.methodBuilder("disable" + typeElement.getSimpleName())
                .addParameter(
                        ParameterSpec.builder(Long.class, "id")
                                .addAnnotation(
                                        AnnotationSpec.builder(PathVariable.class)
                                                .addMember("value", "$S", "id")
                                                .build()
                                )
                                .build()
                )
                .addAnnotation(
                        AnnotationSpec.builder(PostMapping.class)
                                .addMember("value", "$S", "disable/{id}")
                                .build()
                )
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(ParameterizedTypeName.get(ClassName.get(Returns.class), ClassName.get(Void.class)))
                .build());
    }

    private Optional<MethodSpec> findById(DefaultNameContext nameContext) {
        if (StringUtils.containsNull(nameContext.getResponsePackageName())) {
            return Optional.empty();

        }
        return Optional.of(MethodSpec.methodBuilder("findById")
                .addParameter(ParameterSpec.builder(Long.class, "id").addAnnotation(AnnotationSpec.builder(PathVariable.class).addMember("value", "$S", "id").build()).build())
                .addAnnotation(AnnotationSpec.builder(GetMapping.class).addMember("value", "$S", "findById/{id}").build())
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(ParameterizedTypeName.get(ClassName.get(Returns.class), ClassName.get(nameContext.getResponsePackageName(), nameContext.getResponseClassName())))
                .build());
    }

    private Optional<MethodSpec> findByPage(DefaultNameContext nameContext) {
        if (StringUtils.containsNull(nameContext.getPageRequestPackageName())) {
            return Optional.empty();
        }
        return Optional.of(MethodSpec.methodBuilder("findByPage")
                .addParameter(
                        ParameterSpec.builder(ClassName.get(nameContext.getPageRequestPackageName(), nameContext.getPageRequestClassName()), "request")
                                .addAnnotation(RequestBody.class)
                                .build()
                )
                .addAnnotation(AnnotationSpec.builder(PostMapping.class).addMember("value", "$S", "findByPage").build())
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(
                        ParameterizedTypeName.get(
                                ClassName.get(Returns.class),
                                ParameterizedTypeName.get(
                                        ClassName.get(PageResponse.class),
                                        ClassName.get(nameContext.getPageResponsePackageName(), nameContext.getPageResponseClassName())
                                )
                        )
                )
                .build());
    }
}
