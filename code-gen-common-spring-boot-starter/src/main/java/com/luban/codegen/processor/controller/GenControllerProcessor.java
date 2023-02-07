package com.hp.codegen.processor.controller;

import com.google.auto.service.AutoService;
import com.hp.codegen.context.DefaultNameContext;
import com.hp.codegen.processor.AbstractCodeGenProcessor;
import com.hp.codegen.spi.CodeGenProcessor;
import com.hp.codegen.util.StringUtils;
import com.luban.common.core.enums.CodeEnum;
import com.luban.common.core.web.domain.AjaxResult;
import com.squareup.javapoet.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.Optional;

/**
 * @author HP 2022/12/21
 */
@AutoService(value = CodeGenProcessor.class)
public class GenControllerProcessor extends AbstractCodeGenProcessor {

    public static final String CONTROLLER_SUFFIX = "Controller";

    @Override
    protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        DefaultNameContext nameContext = getNameContext(typeElement);
        TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(nameContext.getControllerClassName())
                .addAnnotation(RestController.class)
                .addAnnotation(Slf4j.class)
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
        Optional<MethodSpec> createMethod = createMethod(serviceFieldName, typeElement, nameContext);
        createMethod.ifPresent(m -> typeSpecBuilder.addMethod(m));
        Optional<MethodSpec> updateMethod = updateMethod(serviceFieldName, typeElement, nameContext);
        updateMethod.ifPresent(m -> typeSpecBuilder.addMethod(m));
        Optional<MethodSpec> validMethod = validMethod(serviceFieldName, typeElement);
        validMethod.ifPresent(m -> typeSpecBuilder.addMethod(m));
        Optional<MethodSpec> invalidMethod = inValidMethod(serviceFieldName, typeElement);
        invalidMethod.ifPresent(m -> typeSpecBuilder.addMethod(m));
        Optional<MethodSpec> findById = findById(serviceFieldName, nameContext);
        findById.ifPresent(m -> typeSpecBuilder.addMethod(m));
//        Optional<MethodSpec> findByPage = findByPage(serviceFieldName, nameContext);
//        findByPage.ifPresent(m -> typeSpecBuilder.addMethod(m));
        generateJavaSourceFile(generatePackage(typeElement),
                typeElement.getAnnotation(GenController.class).sourcePath(), typeSpecBuilder);
    }


    private Optional<MethodSpec> createMethod(String serviceFieldName, TypeElement typeElement, DefaultNameContext nameContext) {
        boolean containsNull = StringUtils.containsNull(nameContext.getRequestPackageName(), nameContext.getRequestPackageName(), nameContext.getMapperPackageName());
        if (!containsNull) {
            return Optional.of(MethodSpec.methodBuilder("create" + typeElement.getSimpleName())
                    .addParameter(ParameterSpec.builder(ClassName.get(nameContext.getRequestPackageName(), nameContext.getRequestClassName()), "request").addAnnotation(
                            RequestBody.class).build())
                    .addAnnotation(AnnotationSpec.builder(PostMapping.class).addMember("value", "$S", "create" + typeElement.getSimpleName()).build())
                    .addModifiers(Modifier.PUBLIC)
                    .addCode(
                            CodeBlock.of("$T creator = $T.INSTANCE.requestToDto(request);\n",
                                    ClassName.get(nameContext.getDtoPackageName(), nameContext.getDtoClassName()), ClassName.get(nameContext.getMapperPackageName(), nameContext.getMapperClassName()))
                    )
                    .addCode(CodeBlock.of("return AjaxResult.success($L.create$L(creator));", serviceFieldName, typeElement.getSimpleName().toString()))
                    .addJavadoc("createRequest")
                    .returns(AjaxResult.class)
                    .build());
        }
        return Optional.empty();
    }

    /**
     * 更新方法
     *
     * @param serviceFieldName
     * @param typeElement
     * @param nameContext
     * @return
     */
    private Optional<MethodSpec> updateMethod(String serviceFieldName, TypeElement typeElement, DefaultNameContext nameContext) {
        boolean containsNull = StringUtils.containsNull(nameContext.getRequestPackageName(), nameContext.getRequestClassName(), nameContext.getMapperPackageName());
        if (!containsNull) {
            return Optional.of(MethodSpec.methodBuilder("update" + typeElement.getSimpleName())
                    .addParameter(ParameterSpec.builder(ClassName.get(nameContext.getRequestPackageName(), nameContext.getRequestClassName()), "request").addAnnotation(RequestBody.class).build())
                    .addAnnotation(AnnotationSpec.builder(PostMapping.class).addMember("value", "$S", "update" + typeElement.getSimpleName()).build())
                    .addModifiers(Modifier.PUBLIC)
                    .addCode(
                            CodeBlock.of("$T updater = $T.INSTANCE.requestToDto(request);\n",
                                    ClassName.get(nameContext.getDtoPackageName(), nameContext.getDtoClassName()), ClassName.get(nameContext.getMapperPackageName(), nameContext.getMapperClassName()))
                    )
                    .addCode(
                            CodeBlock.of("$L.update$L(updater);\n", serviceFieldName, typeElement.getSimpleName().toString())
                    )
                    .addCode(
                            CodeBlock.of("return $T.success($T.Success.getName());", AjaxResult.class, CodeEnum.class)
                    )
                    .returns(AjaxResult.class)
                    .build());
        }
        return Optional.empty();
    }

    /**
     * 启用
     *
     * @param serviceFieldName
     * @param typeElement
     * @return
     */
    private Optional<MethodSpec> validMethod(String serviceFieldName, TypeElement typeElement) {
        return Optional.of(MethodSpec.methodBuilder("valid" + typeElement.getSimpleName())
                .addParameter(ParameterSpec.builder(Long.class, "id").addAnnotation(PathVariable.class).build())
                .addAnnotation(AnnotationSpec.builder(PostMapping.class).addMember("value", "$S", "valid/{id}").build())
                .addModifiers(Modifier.PUBLIC)
                .addCode(
                        CodeBlock.of("$L.valid$L(id);\n",
                                serviceFieldName, typeElement.getSimpleName().toString())
                )
                .addCode(
                        CodeBlock.of("return $T.success($T.Success.getName());", AjaxResult.class, CodeEnum.class)
                )
                .returns(AjaxResult.class)
                .build());
    }

    /**
     * 修复不返回方法的问题
     *
     * @param serviceFieldName
     * @param typeElement
     * @return
     */
    private Optional<MethodSpec> inValidMethod(String serviceFieldName, TypeElement typeElement) {
        return Optional.of(MethodSpec.methodBuilder("invalid" + typeElement.getSimpleName())
                .addParameter(ParameterSpec.builder(Long.class, "id").addAnnotation(PathVariable.class).build())
                .addAnnotation(AnnotationSpec.builder(PostMapping.class).addMember("value", "$S", "invalid/{id}").build())
                .addModifiers(Modifier.PUBLIC)
                .addCode(
                        CodeBlock.of("$L.invalid$L(id);\n",
                                serviceFieldName, typeElement.getSimpleName().toString())
                )
                .addCode(
                        CodeBlock.of("return $T.success($T.Success.getName());", AjaxResult.class, CodeEnum.class)
                )
                .returns(AjaxResult.class)
                .build());
    }

    private Optional<MethodSpec> findById(String serviceFieldName, DefaultNameContext nameContext) {
        boolean containsNull = StringUtils.containsNull(nameContext.getVoPackageName(), nameContext.getResponsePackageName(), nameContext.getMapperPackageName());
        if (!containsNull) {
            return Optional.of(MethodSpec.methodBuilder("findById")
                    .addParameter(ParameterSpec.builder(Long.class, "id").addAnnotation(PathVariable.class).build())
                    .addAnnotation(AnnotationSpec.builder(GetMapping.class).addMember("value", "$S", "findById/{id}").build())
                    .addModifiers(Modifier.PUBLIC)
                    .addCode(
                            CodeBlock.of("$T vo = $L.findById(id);\n", ClassName.get(nameContext.getVoPackageName(), nameContext.getVoClassName()), serviceFieldName)
                    )
                    .addCode(
                            CodeBlock.of("$T response = $T.INSTANCE.voToResponse(vo);\n"
                                    , ClassName.get(nameContext.getResponsePackageName(), nameContext.getResponseClassName()),
                                    ClassName.get(nameContext.getMapperPackageName(), nameContext.getMapperClassName()))
                    )
                    .addCode(
                            CodeBlock.of("return $T.success(response);", AjaxResult.class)
                    )
                    .returns(AjaxResult.class)
                    .build());
        }
        return Optional.empty();
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenController.class;
    }

    @Override
    public String generatePackage(TypeElement typeElement) {
        return typeElement.getAnnotation(GenController.class).pkgName();
    }
}
