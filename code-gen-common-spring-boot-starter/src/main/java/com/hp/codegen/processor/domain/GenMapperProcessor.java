package com.hp.codegen.processor.domain;

import cn.hutool.core.util.StrUtil;
import com.google.auto.service.AutoService;
import com.hp.codegen.annotation.domain.GenCreateCommand;
import com.hp.codegen.annotation.domain.GenFindByPageQuery;
import com.hp.codegen.annotation.domain.GenMapper;
import com.hp.codegen.annotation.domain.GenUpdateCommand;
import com.hp.codegen.annotation.model.*;
import com.hp.codegen.annotation.model.*;
import com.hp.codegen.context.CodeGenContextHolder;
import com.hp.codegen.spi.CodeGenProcessor;
import com.hp.codegen.util.CodeGenHelper;
import com.hp.common.base.enums.BaseEnum;
import com.squareup.javapoet.*;
import org.mapstruct.factory.Mappers;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;

/**
 * @author hp
 */
@AutoService(value = CodeGenProcessor.class)
public class GenMapperProcessor extends AbstractDomainCodeGenProcessor {

    @Override
    protected boolean isInterface() {
        return true;
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenMapper.class;
    }

    @Override
    protected void customizeType(TypeElement typeElement, RoundEnvironment roundEnvironment, TypeSpec.Builder typeSpecBuilder) {
        typeSpecBuilder
                .addModifiers(Modifier.PUBLIC)
                .addField(
                        FieldSpec
                                .builder(this.currentGeneratingClassName, "INSTANCE")
                                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                                .initializer(
                                        "$T.getMapper($T.class)",
                                        ClassName.get(Mappers.class),
                                        this.currentGeneratingClassName
                                )
                                .build()
                );

        createCopyMethod().ifPresent(typeSpecBuilder::addMethod);
        createRequestToCreateCommandMethod().ifPresent(typeSpecBuilder::addMethod);
        createCreateCommandToEntityMethod().ifPresent(typeSpecBuilder::addMethod);
        createRequestToUpdateCommandMethod().ifPresent(typeSpecBuilder::addMethod);
        createEntityToResponseMethod().ifPresent(typeSpecBuilder::addMethod);
        createEntityToCustomResponseMethod().ifPresent(typeSpecBuilder::addMethod);
        createEntityToPageResponseMethod().ifPresent(typeSpecBuilder::addMethod);
        createEntityToCustomPageResponseMethod().ifPresent(typeSpecBuilder::addMethod);
        createRequestToPageQueryMethod().ifPresent(typeSpecBuilder::addMethod);
    }

    protected Optional<MethodSpec> createCopyMethod() {
        final MethodSpec methodSpec = MethodSpec.methodBuilder("copy")
                .addParameter(CodeGenContextHolder.getCurrentTypeClassName(), "entity")
                .returns(CodeGenContextHolder.getCurrentTypeClassName())
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .build();
        return Optional.of(methodSpec);
    }

    protected Optional<MethodSpec> createRequestToCreateCommandMethod() {
        final String methodName = "requestToCreateCommand";
        if (CodeGenContextHolder.missingAnyAnnotated(
                GenCreateRequest.class,
                GenCreateCommand.class
        )) {
            CodeGenContextHolder.log(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    String.format("To generate %s() for %s requires @GenCreateRequest and @GenCreateCommand.", methodName, this.currentGeneratingTypeName)
            );
            return Optional.empty();
        }
        final MethodSpec methodSpec = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(
                        CodeGenHelper.createParameterSpecBuilder(GenCreateRequest.class, "request")
                                .build()
                )
                .returns(CodeGenContextHolder.getClassName(GenCreateCommand.class))
                .build();

        return Optional.of(methodSpec);
    }

    protected Optional<MethodSpec> createCreateCommandToEntityMethod() {
        final String methodName = "createCommandToEntity";
        if (!CodeGenContextHolder.isAnnotationPresent(GenCreateCommand.class)) {
            CodeGenContextHolder.log(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    String.format("To generate %s() for %s requires @GenCreateCommand.", methodName, this.currentGeneratingTypeName)
            );
            return Optional.empty();
        }
        final MethodSpec methodSpec = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(
                        CodeGenHelper.createParameterSpecBuilder(GenCreateCommand.class, "command")
                                .build()
                )
                .returns(CodeGenContextHolder.getCurrentTypeClassName())
                .build();

        return Optional.of(methodSpec);
    }

    protected Optional<MethodSpec> createRequestToUpdateCommandMethod() {
        final String methodName = "requestToUpdateCommand";
        if (CodeGenContextHolder.missingAnyAnnotated(
                GenUpdateRequest.class,
                GenUpdateCommand.class
        )) {
            CodeGenContextHolder.log(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    String.format("To generate %s() for %s requires @GenUpdateRequest and @GenUpdateCommand.", methodName, this.currentGeneratingTypeName)
            );
            return Optional.empty();
        }

        final MethodSpec methodSpec = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(
                        CodeGenHelper.createParameterSpecBuilder(GenUpdateRequest.class, "request")
                                .build()
                )
                .returns(CodeGenContextHolder.getClassName(GenUpdateCommand.class))
                .build();

        return Optional.of(methodSpec);
    }

    protected Optional<MethodSpec> createEntityToResponseMethod() {
        final String methodName = "entityToResponse";
        if (!CodeGenContextHolder.isAnnotationPresent(GenResponse.class)) {
            CodeGenContextHolder.log(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    String.format("To generate %s() for %s requires @GenResponse.", methodName, this.currentGeneratingTypeName)
            );
            return Optional.empty();
        }

        final MethodSpec methodSpec = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(CodeGenContextHolder.getCurrentTypeClassName(), "entity")
                .returns(CodeGenContextHolder.getClassName(GenResponse.class))
                .build();

        return Optional.of(methodSpec);
    }

    protected Optional<MethodSpec> createEntityToCustomResponseMethod() {
        final String methodName = "entityToCustomResponse";
        if (!CodeGenContextHolder.isAnnotationPresent(GenResponse.class)) {
            CodeGenContextHolder.log(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    String.format("To generate %s() for %s requires @GenResponse.", methodName, this.currentGeneratingTypeName)
            );
            return Optional.empty();
        }
        final MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.DEFAULT, Modifier.PUBLIC)
                .addParameter(CodeGenContextHolder.getCurrentTypeClassName(), "entity")
                .returns(CodeGenContextHolder.getClassName(GenResponse.class))
                .addCode(
                        CodeBlock.of("""
                                        final $T response = entityToResponse(entity);
                                        // customization
                                        """,
                                CodeGenContextHolder.getClassName(GenResponse.class)
                        )
                );

        addMapStructMappingModeCodeBlock(methodSpecBuilder);

        final MethodSpec methodSpec = methodSpecBuilder
                .addCode(
                        CodeBlock.of("return response;")
                )
                .returns(CodeGenContextHolder.getClassName(GenResponse.class))
                .build();
        return Optional.of(methodSpec);
    }

    protected Optional<MethodSpec> createEntityToPageResponseMethod() {
        final String methodName = "entityToPageResponse";
        if (!CodeGenContextHolder.isAnnotationPresent(GenPageResponse.class)) {
            CodeGenContextHolder.log(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    String.format("To generate %s() for %s requires @GenPageResponse.", methodName, this.currentGeneratingTypeName)
            );
            return Optional.empty();
        }

        final MethodSpec methodSpec = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(CodeGenContextHolder.getCurrentTypeClassName(), "entity")
                .returns(CodeGenContextHolder.getClassName(GenPageResponse.class))
                .build();

        return Optional.of(methodSpec);
    }

    protected Optional<MethodSpec> createEntityToCustomPageResponseMethod() {
        final String methodName = "entityToCustomPageResponse";
        if (!CodeGenContextHolder.isAnnotationPresent(GenPageResponse.class)) {
            CodeGenContextHolder.log(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    String.format("To generate %s() for %s requires @GenPageResponse.", methodName, this.currentGeneratingTypeName)
            );
            return Optional.empty();
        }

        final MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.DEFAULT, Modifier.PUBLIC)
                .addParameter(CodeGenContextHolder.getCurrentTypeClassName(), "entity")
                .addCode(
                        CodeBlock.of("""
                                        final $T response = entityToPageResponse(entity);
                                        // customization
                                        """,
                                CodeGenContextHolder.getClassName(GenPageResponse.class)
                        )
                );

        addMapStructMappingModeCodeBlock(methodSpecBuilder);

        final MethodSpec methodSpec = methodSpecBuilder
                .addCode("return response;")
                .returns(CodeGenContextHolder.getClassName(GenPageResponse.class))
                .build();

        return Optional.of(methodSpec);
    }

    protected Optional<MethodSpec> createRequestToPageQueryMethod() {
        final String methodName = "requestToPageQuery";
        if (!CodeGenContextHolder.isAnnotationPresent(GenFindByPageQuery.class)) {
            CodeGenContextHolder.log(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    String.format("To generate %s() for %s requires @GenPageResponse.", methodName, this.currentGeneratingTypeName)
            );
            return Optional.empty();
        }

        final MethodSpec methodSpec = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(CodeGenContextHolder.getClassName(GenPageRequest.class), "request")
                .returns(CodeGenContextHolder.getClassName(GenFindByPageQuery.class))
                .build();

        return Optional.of(methodSpec);
    }

    protected void addMapStructMappingModeCodeBlock(MethodSpec.Builder methodSpecBuilder) {
        if (!CodeGenContextHolder.isMapStructMappingMode()) {
            return;
        }
        final List<VariableElement> fields = CodeGenHelper.findNotIgnoreAndNotDeprecatedFields(CodeGenContextHolder.getCurrentTypeElement());
        fields.stream().filter(CodeGenHelper::isBaseEnumType)
                .forEach(field -> {
                    final String fieldName = StrUtil.upperFirst(field.getSimpleName().toString());
                    methodSpecBuilder.addCode(
                            CodeBlock.of(
                                    "response.set$LName($T.ofNullable(entity.get$L()).map($T::getName).orElse(null));\n",
                                    fieldName,
                                    ClassName.get(Optional.class),
                                    fieldName,
                                    ClassName.get(BaseEnum.class)
                            )
                    );
                });
    }
}
