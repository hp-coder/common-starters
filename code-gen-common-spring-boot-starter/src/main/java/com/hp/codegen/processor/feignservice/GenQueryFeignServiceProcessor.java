package com.hp.codegen.processor.feignservice;

import com.google.auto.service.AutoService;
import com.google.common.collect.Lists;
import com.hp.codegen.annotation.api.GenQueryApi;
import com.hp.codegen.annotation.app.GenQueryAppService;
import com.hp.codegen.annotation.domain.GenFindByIdQuery;
import com.hp.codegen.annotation.domain.GenFindByPageQuery;
import com.hp.codegen.annotation.domain.GenMapper;
import com.hp.codegen.annotation.model.GenPageRequest;
import com.hp.codegen.annotation.model.GenPageResponse;
import com.hp.codegen.annotation.model.GenResponse;
import com.hp.codegen.annotation.feignservice.GenQueryFeignService;
import com.hp.codegen.context.CodeGenContextHolder;
import com.hp.codegen.spi.CodeGenProcessor;
import com.hp.codegen.util.CodeGenHelper;
import com.hp.common.base.model.PageRequestWrapper;
import com.hp.common.base.model.PageResponse;
import com.hp.common.base.model.Returns;
import com.squareup.javapoet.*;
import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;

/**
 * @author hp
 */
@AutoService(value = CodeGenProcessor.class)
public class GenQueryFeignServiceProcessor extends AbstractFeignServiceCodeGenProcessor {

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenQueryFeignService.class;
    }

    @Override
    protected boolean creatable(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        if (CodeGenContextHolder.missingAnyAnnotated(
                GenQueryApi.class,
                GenMapper.class
        )) {
            CodeGenContextHolder.log(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    String.format("To generate %s requires @GenQueryApi and @GenMapper.", this.currentGeneratingTypeName)
            );
            return false;
        }
        return true;
    }

    @Override
    protected void customizeType(TypeElement typeElement, RoundEnvironment roundEnvironment, TypeSpec.Builder typeSpecBuilder) {
        typeSpecBuilder
                .addAnnotation(Slf4j.class)
                .addAnnotation(Validated.class)
                .addAnnotation(RestController.class)
                .addAnnotation(RequiredArgsConstructor.class)
                .addAnnotation(
                        AnnotationSpec.builder(RequestMapping.class)
                                .addMember("value", "$T.PATH", CodeGenContextHolder.getClassName(GenQueryApi.class))
                                .build()
                )
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(
                        CodeGenContextHolder.getClassName(GenQueryApi.class)
                );

        final List<FieldSpec> injectionFields = createInjectionFields();
        typeSpecBuilder.addFields(injectionFields);

        createFindByIdMethod().ifPresent(typeSpecBuilder::addMethod);
        createFindByPageMethod().ifPresent(typeSpecBuilder::addMethod);

    }

    @Nonnull
    private static List<FieldSpec> createInjectionFields() {
        final List<FieldSpec> fieldSpecs = Lists.newArrayList();
        if (CodeGenContextHolder.isAnnotationPresent(GenQueryAppService.class)) {
            final FieldSpec serviceInjectionField = CodeGenHelper.createFieldSpecBuilder(GenQueryAppService.class)
                    .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                    .build();
            fieldSpecs.add(serviceInjectionField);
        }

        return fieldSpecs;
    }

    private Optional<MethodSpec> createFindByIdMethod() {
        if (CodeGenContextHolder.missingAnyAnnotated(
                GenQueryAppService.class,
                GenResponse.class
        )) {
            CodeGenContextHolder.log(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    String.format("To generate findById() for %s requires @GenQueryAppService and @GenResponse.", this.currentGeneratingTypeName)
            );
            return Optional.empty();
        }
        final MethodSpec methodSpec = MethodSpec.methodBuilder("findById")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(
                        ParameterSpec.builder(Long.class, "id")
//                                .addAnnotation(PathVariable.class)
                                .build()
                )
                .addCode(
                        CodeBlock.of(
                                "final $T query = new $T(id);\n",
                                CodeGenContextHolder.getClassName(GenFindByIdQuery.class),
                                CodeGenContextHolder.getClassName(GenFindByIdQuery.class)
                        )
                )
                .addCode(
                        CodeBlock.of(
                                "final $T response = $L.find$LById(query);\n",
                                CodeGenContextHolder.getClassName(GenResponse.class),
                                CodeGenContextHolder.getClassFieldName(GenQueryAppService.class),
                                CodeGenContextHolder.getCurrentTypeName()
                        )
                )
                .addCode(
                        CodeBlock.of(
                                "return $T.success(response);",
                                ClassName.get(Returns.class)
                        )
                )
                .returns(
                        ParameterizedTypeName.get(
                                ClassName.get(Returns.class),
                                CodeGenContextHolder.getClassName(GenResponse.class)
                        )
                )
                .build();
        return Optional.of(methodSpec);
    }

    private Optional<MethodSpec> createFindByPageMethod() {
        if (CodeGenContextHolder.missingAnyAnnotated(
                GenQueryAppService.class,
                GenPageRequest.class,
                GenPageResponse.class
        )) {
            CodeGenContextHolder.log(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    String.format("To generate findByPage() for %s requires @GenQueryAppService @GenPageRequest and @GenPageResponse.", this.currentGeneratingTypeName)
            );
            return Optional.empty();
        }

        final MethodSpec methodSpec = MethodSpec.methodBuilder("findByPage")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(
                        ParameterSpec.builder(
                                        ParameterizedTypeName.get(
                                                ClassName.get(PageRequestWrapper.class),
                                                CodeGenContextHolder.getClassName(GenPageRequest.class)
                                        ),
                                        "requestWrapper"
                                )
//                                .addAnnotation(RequestBody.class)
                                .addAnnotation(Valid.class)
                                .build()
                )
                .addCode(
                        CodeBlock.of(
                                "final $T pageQueryWrapper = requestWrapper.convert($T.INSTANCE::requestToPageQuery);\n",
                                ParameterizedTypeName.get(
                                        ClassName.get(PageRequestWrapper.class),
                                        CodeGenContextHolder.getClassName(GenFindByPageQuery.class)
                                ),
                                CodeGenContextHolder.getClassName(GenMapper.class)
                        )
                )
                .addCode(
                        CodeBlock.of(
                                "return $T.success($L.find$LByPage(pageQueryWrapper));",
                                Returns.class,
                                CodeGenContextHolder.getClassFieldName(GenQueryAppService.class),
                                CodeGenContextHolder.getCurrentTypeName()
                        )
                )
                .returns(
                        ParameterizedTypeName.get(
                                ClassName.get(Returns.class),
                                ParameterizedTypeName.get(
                                        ClassName.get(PageResponse.class),
                                        CodeGenContextHolder.getClassName(GenPageResponse.class)
                                )
                        )
                )
                .build();

        return Optional.of(methodSpec);
    }
}
