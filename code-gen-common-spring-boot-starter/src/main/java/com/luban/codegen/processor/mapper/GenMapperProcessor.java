package com.luban.codegen.processor.mapper;

import com.google.auto.service.AutoService;
import com.luban.codegen.context.DefaultNameContext;
import com.luban.codegen.processor.AbstractCodeGenProcessor;
import com.luban.codegen.spi.CodeGenProcessor;
import com.luban.codegen.util.StringUtils;
import com.luban.common.base.mapper.DateMapper;
import com.luban.common.base.mapper.GenericEnumMapper;
import com.squareup.javapoet.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.Optional;

/**
 * @author hp
 */
@AutoService(value = CodeGenProcessor.class)
public class GenMapperProcessor extends AbstractCodeGenProcessor {

    public static final String SUFFIX = "Mapper";

    @Override
    protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        final String className = typeElement.getSimpleName() + SUFFIX;
        final String packageName = typeElement.getAnnotation(GenMapper.class).pkgName();
        final AnnotationSpec mapperAnnotation = AnnotationSpec.builder(Mapper.class)
                .addMember("uses", "$T.class", ClassName.get(GenericEnumMapper.class))
                .addMember("uses", "$T.class", ClassName.get(DateMapper.class))
                .build();
        final TypeSpec.Builder typeSpecBuilder = TypeSpec.interfaceBuilder(className)
                .addAnnotation(mapperAnnotation)
                .addModifiers(Modifier.PUBLIC);
        final FieldSpec instance = FieldSpec
                .builder(ClassName.get(packageName, className), "INSTANCE")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer(
                        "$T.getMapper($T.class)",
                        ClassName.get(Mappers.class),
                        ClassName.get(packageName, className)
                )
                .build();
        typeSpecBuilder.addField(instance);

        final DefaultNameContext nameContext = getNameContext();

        copy(typeElement, nameContext).ifPresent(typeSpecBuilder::addMethod);
        requestToCreateCommandMethod(nameContext).ifPresent(typeSpecBuilder::addMethod);
        createCommandToEntityMethod(typeElement, nameContext).ifPresent(typeSpecBuilder::addMethod);
        requestToUpdateCommandMethod(nameContext).ifPresent(typeSpecBuilder::addMethod);
        entityToResponseMethod(typeElement, nameContext).ifPresent(typeSpecBuilder::addMethod);
        entityToCustomResponseMethod(typeElement, nameContext).ifPresent(typeSpecBuilder::addMethod);
        entityToPageResponseMethod(typeElement, nameContext).ifPresent(typeSpecBuilder::addMethod);
        entityToCustomPageResponseMethod(typeElement, nameContext).ifPresent(typeSpecBuilder::addMethod);
        dtoToEntityMethod(typeElement, nameContext).ifPresent(typeSpecBuilder::addMethod);
        requestToDtoMethod(nameContext).ifPresent(typeSpecBuilder::addMethod);
        voToResponseMethod(nameContext).ifPresent(typeSpecBuilder::addMethod);
        voToCustomResponseMethod(nameContext).ifPresent(typeSpecBuilder::addMethod);

        generateJavaSourceFile(generatePackage(typeElement), typeElement.getAnnotation(GenMapper.class).sourcePath(), typeSpecBuilder);
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenMapper.class;
    }

    @Override
    public String generatePackage(TypeElement typeElement) {
        return typeElement.getAnnotation(GenMapper.class).pkgName();
    }

    private Optional<MethodSpec> copy(TypeElement typeElement, DefaultNameContext nameContext) {
        if (StringUtils.containsNull(nameContext.getResponsePackageName())) {
            return Optional.empty();
        }
        return Optional.of(
                MethodSpec.methodBuilder("copy")
                        .addParameter(ClassName.get(typeElement), "entity")
                        .returns(ClassName.get(typeElement))
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT).build()
        );
    }

    private Optional<MethodSpec> requestToCreateCommandMethod(DefaultNameContext nameContext) {
        if (StringUtils.containsNull(nameContext.getCreateCommandPackageName(), nameContext.getCreateRequestPackageName())) {
            return Optional.empty();
        }
        return Optional.of(
                MethodSpec.methodBuilder("requestToCreateCommand")
                        .addParameter(ClassName.get(nameContext.getCreateRequestPackageName(), nameContext.getCreateRequestClassName()), "request")
                        .returns(ClassName.get(nameContext.getCreateCommandPackageName(), nameContext.getCreateCommandClassName()))
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT).build()
        );
    }

    private Optional<MethodSpec> createCommandToEntityMethod(TypeElement typeElement, DefaultNameContext nameContext) {
        if (StringUtils.containsNull(nameContext.getCreateCommandPackageName(), nameContext.getCreateRequestPackageName())) {
            return Optional.empty();
        }
        return Optional.of(
                MethodSpec.methodBuilder("createCommandToEntity")
                        .addParameter(ClassName.get(nameContext.getCreateCommandPackageName(), nameContext.getCreateCommandClassName()), "command")
                        .returns(ClassName.get(typeElement)).addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .build()
        );
    }

    private Optional<MethodSpec> requestToUpdateCommandMethod(DefaultNameContext nameContext) {
        if (StringUtils.containsNull(nameContext.getUpdateCommandPackageName(), nameContext.getUpdateRequestPackageName())) {
            return Optional.empty();
        }
        return Optional.of(
                MethodSpec.methodBuilder("requestToUpdateCommand")
                        .addParameter(ClassName.get(nameContext.getUpdateRequestPackageName(), nameContext.getUpdateRequestClassName()), "request")
                        .returns(ClassName.get(nameContext.getUpdateCommandPackageName(), nameContext.getUpdateCommandClassName()))
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .build()
        );
    }

    private Optional<MethodSpec> entityToResponseMethod(TypeElement typeElement, DefaultNameContext nameContext) {
        if (StringUtils.containsNull(nameContext.getResponsePackageName())) {
            return Optional.empty();
        }
        return Optional.of(MethodSpec.methodBuilder("entityToResponse").addParameter(ClassName.get(typeElement), "entity").returns(ClassName.get(nameContext.getResponsePackageName(), nameContext.getResponseClassName())).addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT).build());
    }

    private Optional<MethodSpec> entityToCustomResponseMethod(TypeElement typeElement, DefaultNameContext nameContext) {
        if (StringUtils.containsNull(nameContext.getResponsePackageName())) {
            return Optional.empty();
        }
        return Optional.of(
                MethodSpec.methodBuilder("entityToCustomResponse")
                        .addParameter(ClassName.get(typeElement), "entity")
                        .returns(ClassName.get(nameContext.getResponsePackageName(), nameContext.getResponseClassName()))
                        .addModifiers(Modifier.DEFAULT, Modifier.PUBLIC)
                        .addCode(CodeBlock.of("final $T response = entityToResponse(entity);\n" + "// customization\n" + "return response;", ClassName.get(nameContext.getResponsePackageName(), nameContext.getResponseClassName())))
                        .returns(ClassName.get(nameContext.getResponsePackageName(), nameContext.getResponseClassName()))
                        .build()
        );
    }

    private Optional<MethodSpec> entityToPageResponseMethod(TypeElement typeElement, DefaultNameContext nameContext) {
        if (StringUtils.containsNull(nameContext.getPageResponsePackageName())) {
            return Optional.empty();
        }
        return Optional.of(
                MethodSpec.methodBuilder("entityToPageResponse")
                        .addParameter(ClassName.get(typeElement), "entity")
                        .returns(ClassName.get(nameContext.getPageResponsePackageName(), nameContext.getPageResponseClassName()))
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .build()
        );
    }

    private Optional<MethodSpec> entityToCustomPageResponseMethod(TypeElement typeElement, DefaultNameContext nameContext) {
        if (StringUtils.containsNull(nameContext.getPageResponsePackageName())) {
            return Optional.empty();
        }
        return Optional.of(
                MethodSpec.methodBuilder("entityToCustomPageResponse")
                        .addParameter(ClassName.get(typeElement), "entity")
                        .returns(ClassName.get(nameContext.getPageResponsePackageName(), nameContext.getPageResponseClassName()))
                        .addModifiers(Modifier.DEFAULT, Modifier.PUBLIC)
                        .addCode(CodeBlock.of("final $T response = entityToPageResponse(entity);\n" + "// customization\n" + "return response;", ClassName.get(nameContext.getPageResponsePackageName(), nameContext.getPageResponseClassName())))
                        .build()
        );
    }

    private Optional<MethodSpec> dtoToEntityMethod(TypeElement typeElement, DefaultNameContext nameContext) {
        if (StringUtils.containsNull(nameContext.getDtoPackageName())) {
            return Optional.empty();
        }
        return Optional.of(
                MethodSpec.methodBuilder("dtoToEntity")
                        .returns(ClassName.get(typeElement)).addParameter(ClassName.get(nameContext.getDtoPackageName(), nameContext.getDtoClassName()), "dto")
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .build()
        );
    }

    private Optional<MethodSpec> requestToDtoMethod(DefaultNameContext nameContext) {
        if (StringUtils.containsNull(nameContext.getDtoPackageName(), nameContext.getRequestPackageName())) {
            return Optional.empty();
        }
        if (StringUtils.notEmpty(nameContext.getCreateCommandPackageName(), nameContext.getCreateCommandClassName())) {
            return Optional.empty();
        }
        return Optional.of(
                MethodSpec.methodBuilder("requestToDto")
                        .addParameter(ClassName.get(nameContext.getRequestPackageName(), nameContext.getRequestClassName()), "request")
                        .returns(ClassName.get(nameContext.getDtoPackageName(), nameContext.getDtoClassName()))
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .build()
        );
    }

    private Optional<MethodSpec> voToResponseMethod(DefaultNameContext nameContext) {
        if (StringUtils.containsNull(nameContext.getResponsePackageName(), nameContext.getVoPackageName())) {
            return Optional.empty();
        }
        return Optional.of(
                MethodSpec.methodBuilder("voToResponse")
                        .addParameter(ClassName.get(nameContext.getVoPackageName(), nameContext.getVoClassName()), "vo")
                        .returns(ClassName.get(nameContext.getResponsePackageName(), nameContext.getResponseClassName()))
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .build()
        );
    }

    private Optional<MethodSpec> voToCustomResponseMethod(DefaultNameContext nameContext) {
        if (StringUtils.containsNull(nameContext.getResponsePackageName(), nameContext.getVoPackageName())) {
            return Optional.empty();
        }
        return Optional.of(
                MethodSpec.methodBuilder("voToCustomResponse")
                        .addParameter(ClassName.get(nameContext.getVoPackageName(), nameContext.getVoClassName()), "vo")
                        .addCode(CodeBlock.of("final $T response = voToResponse(vo);\n" + "// customization\n" + "return response;", ClassName.get(nameContext.getResponsePackageName(), nameContext.getResponseClassName())))
                        .returns(ClassName.get(nameContext.getResponsePackageName(), nameContext.getResponseClassName()))
                        .addModifiers(Modifier.PUBLIC, Modifier.DEFAULT)
                        .build()
        );
    }
}
