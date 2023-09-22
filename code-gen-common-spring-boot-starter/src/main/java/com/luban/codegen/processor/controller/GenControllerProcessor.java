package com.luban.codegen.processor.controller;

import com.google.auto.service.AutoService;
import com.luban.codegen.constant.Orm;
import com.luban.codegen.context.DefaultNameContext;
import com.luban.codegen.processor.AbstractCodeGenProcessor;
import com.luban.codegen.spi.CodeGenProcessor;
import com.luban.codegen.util.StringUtils;
import com.luban.common.base.enums.CodeEnum;
import com.luban.common.base.model.Returns;
import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author hp
 */
@AutoService(value = CodeGenProcessor.class)
public class GenControllerProcessor extends AbstractCodeGenProcessor {

    public static final String CONTROLLER_SUFFIX = "Controller";

    @Override
    public boolean supportedOrm(Orm orm) {
        return Arrays.asList(Orm.values()).contains(orm);
    }

    @Override
    protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        DefaultNameContext nameContext = getNameContext();
        TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(nameContext.getControllerClassName())
                .addAnnotation(Slf4j.class)
                .addAnnotation(RestController.class)
                .addAnnotation(AnnotationSpec.builder(RequestMapping.class).addMember("value", "$S", StringUtils.camel(typeElement.getSimpleName().toString()) + "/v1").build())
                .addAnnotation(RequiredArgsConstructor.class)
                .addModifiers(Modifier.PUBLIC);
        String serviceFieldName = StringUtils.camel(typeElement.getSimpleName().toString()) + "Service";
        if (StringUtils.containsNull(nameContext.getServicePackageName())) {
            return;
        }
        FieldSpec serviceField = FieldSpec
                .builder(ClassName.get(nameContext.getServicePackageName(), nameContext.getServiceClassName()), serviceFieldName)
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build();

        typeSpecBuilder.addField(serviceField);

        createMethod(serviceFieldName, typeElement, nameContext).ifPresent(typeSpecBuilder::addMethod);
        updateMethod(serviceFieldName, typeElement, nameContext).ifPresent(typeSpecBuilder::addMethod);
        enableMethod(serviceFieldName, typeElement).ifPresent(typeSpecBuilder::addMethod);
        disableMethod(serviceFieldName, typeElement).ifPresent(typeSpecBuilder::addMethod);
        findById(serviceFieldName, typeElement, nameContext).ifPresent(typeSpecBuilder::addMethod);

        generateJavaSourceFile(generatePackage(typeElement), generatePath(typeElement), typeSpecBuilder);
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenController.class;
    }

    @Override
    public String generatePackage(TypeElement typeElement) {
        return typeElement.getAnnotation(GenController.class).pkgName();
    }

    @Override
    public String generatePath(TypeElement typeElement) {
        return typeElement.getAnnotation(GenController.class).sourcePath();
    }

    private Optional<MethodSpec> createMethod(String serviceFieldName, TypeElement typeElement, DefaultNameContext nameContext) {
        boolean containsNull = StringUtils.containsNull(nameContext.getRequestPackageName(), nameContext.getRequestPackageName(), nameContext.getMapperPackageName());
        if (!containsNull) {
            return Optional.of(MethodSpec.methodBuilder("create" + typeElement.getSimpleName())
                    .addParameter(ParameterSpec.builder(ClassName.get(nameContext.getRequestPackageName(), nameContext.getRequestClassName()), "request").addAnnotation(
                            RequestBody.class).build())
                    .addAnnotation(AnnotationSpec.builder(PostMapping.class).addMember("value", "$S", "_create").build())
                    .addModifiers(Modifier.PUBLIC)
                    .addCode(
                            CodeBlock.of("$T creator = $T.INSTANCE.requestToDto(request);\n",
                                    ClassName.get(nameContext.getDtoPackageName(), nameContext.getDtoClassName()), ClassName.get(nameContext.getMapperPackageName(), nameContext.getMapperClassName()))
                    )
                    .addCode(CodeBlock.of("return Returns.success().data($L.create$L(creator));", serviceFieldName, typeElement.getSimpleName().toString()))
                    .returns(Returns.class)
                    .build());
        }
        return Optional.empty();
    }

    private Optional<MethodSpec> updateMethod(String serviceFieldName, TypeElement typeElement, DefaultNameContext nameContext) {
        boolean containsNull = StringUtils.containsNull(nameContext.getRequestPackageName(), nameContext.getRequestClassName(), nameContext.getMapperPackageName());
        if (!containsNull) {
            return Optional.of(MethodSpec.methodBuilder("update" + typeElement.getSimpleName())
                    .addParameter(ParameterSpec.builder(ClassName.get(nameContext.getRequestPackageName(), nameContext.getRequestClassName()), "request").addAnnotation(RequestBody.class).build())
                    .addAnnotation(AnnotationSpec.builder(PostMapping.class).addMember("value", "$S", "_update").build())
                    .addModifiers(Modifier.PUBLIC)
                    .addCode(
                            CodeBlock.of("$T updater = $T.INSTANCE.requestToDto(request);\n",
                                    ClassName.get(nameContext.getDtoPackageName(), nameContext.getDtoClassName()), ClassName.get(nameContext.getMapperPackageName(), nameContext.getMapperClassName()))
                    )
                    .addCode(
                            CodeBlock.of("$L.update$L(updater);\n", serviceFieldName, typeElement.getSimpleName().toString())
                    )
                    .addCode(
                            CodeBlock.of("return $T.success().data($T.Success.getName());", Returns.class, CodeEnum.class)
                    )
                    .returns(Returns.class)
                    .build());
        }
        return Optional.empty();
    }

    private Optional<MethodSpec> enableMethod(String serviceFieldName, TypeElement typeElement) {
        return Optional.of(MethodSpec.methodBuilder("enable" + typeElement.getSimpleName())
                .addParameter(ParameterSpec.builder(Long.class, "id").addAnnotation(PathVariable.class).build())
                .addAnnotation(AnnotationSpec.builder(PostMapping.class).addMember("value", "$S", "enable/{id}").build())
                .addModifiers(Modifier.PUBLIC)
                .addCode(
                        CodeBlock.of("$L.enable$L(id);\n",
                                serviceFieldName, typeElement.getSimpleName().toString())
                )
                .addCode(
                        CodeBlock.of("return $T.success().data($T.Success.getName());", Returns.class, CodeEnum.class)
                )
                .returns(Returns.class)
                .build());
    }

    private Optional<MethodSpec> disableMethod(String serviceFieldName, TypeElement typeElement) {
        return Optional.of(MethodSpec.methodBuilder("disable" + typeElement.getSimpleName())
                .addParameter(ParameterSpec.builder(Long.class, "id").addAnnotation(PathVariable.class).build())
                .addAnnotation(AnnotationSpec.builder(PostMapping.class).addMember("value", "$S", "disable/{id}").build())
                .addModifiers(Modifier.PUBLIC)
                .addCode(
                        CodeBlock.of("$L.disable$L(id);\n",
                                serviceFieldName, typeElement.getSimpleName().toString())
                )
                .addCode(
                        CodeBlock.of("return $T.success().data($T.Success.getName());", Returns.class, CodeEnum.class)
                )
                .returns(Returns.class)
                .build());
    }

    private Optional<MethodSpec> findById(String serviceFieldName, TypeElement typeElement, DefaultNameContext nameContext) {
        return Optional.of(MethodSpec.methodBuilder("findById")
                .addParameter(ParameterSpec.builder(Long.class, "id").addAnnotation(PathVariable.class).build())
                .addAnnotation(AnnotationSpec.builder(GetMapping.class).addMember("value", "$S", "findById/{id}").build())
                .addModifiers(Modifier.PUBLIC)
                .addCode(
                        CodeBlock.of("$T entity = $L.findById(id);\n", ClassName.get(typeElement), serviceFieldName)
                )
                .addCode(
                        CodeBlock.of("$T response = $T.INSTANCE.entityToResponse(entity);\n"
                                , ClassName.get(nameContext.getResponsePackageName(), nameContext.getResponseClassName()),
                                ClassName.get(nameContext.getMapperPackageName(), nameContext.getMapperClassName()))
                )
                .addCode(
                        CodeBlock.of("return $T.success().data(response);", Returns.class)
                )
                .returns(Returns.class)
                .build());
    }
}
