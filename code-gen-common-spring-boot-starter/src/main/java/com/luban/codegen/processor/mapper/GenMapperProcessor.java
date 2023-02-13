package com.luban.codegen.processor.mapper;

import com.google.auto.service.AutoService;
import com.luban.codegen.context.DefaultNameContext;
import com.luban.codegen.processor.AbstractCodeGenProcessor;
import com.luban.codegen.spi.CodeGenProcessor;
import com.luban.codegen.util.StringUtils;
import com.luban.common.base.mapper.DateMapper;
import com.luban.common.base.mapper.GenericEnumMapper;
import com.squareup.javapoet.*;
import org.apache.catalina.mapper.Mapper;
import org.mapstruct.factory.Mappers;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.Optional;

/**
 * @Author: Gim
 * @Date: 2019/11/25 14:14
 * @Description:
 */
@AutoService(value = CodeGenProcessor.class)
public class GenMapperProcessor extends AbstractCodeGenProcessor {

    public static final String SUFFIX = "Mapper";

    @Override
    protected void generateClass(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        String className = typeElement.getSimpleName() + SUFFIX;
        String packageName = typeElement.getAnnotation(GenMapper.class).pkgName();
        AnnotationSpec mapperAnnotation = AnnotationSpec.builder(Mapper.class)
                .addMember("uses", "$T.class", GenericEnumMapper.class)
                .addMember("uses", "$T.class", DateMapper.class)
                .build();
        TypeSpec.Builder typeSpecBuilder = TypeSpec.interfaceBuilder(className)
                .addAnnotation(mapperAnnotation)
                .addModifiers(Modifier.PUBLIC);
        FieldSpec instance = FieldSpec
                .builder(ClassName.get(packageName, className), "INSTANCE")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$T.getMapper($T.class)",
                        Mappers.class, ClassName.get(packageName, className))
                .build();
        typeSpecBuilder.addField(instance);
        DefaultNameContext nameContext = getNameContext(typeElement);

        Optional<MethodSpec> dtoToEntityMethod = dtoToEntityMethod(typeElement, nameContext);
        dtoToEntityMethod.ifPresent(m -> typeSpecBuilder.addMethod(m));

        Optional<MethodSpec> request2DtoMethod = request2DtoMethod(nameContext);
        request2DtoMethod.ifPresent(m -> typeSpecBuilder.addMethod(m));

        Optional<MethodSpec> vo2ResponseMethod = vo2ResponseMethod(nameContext);
        vo2ResponseMethod.ifPresent(m -> typeSpecBuilder.addMethod(m));

        generateJavaSourceFile(generatePackage(typeElement),
                typeElement.getAnnotation(GenMapper.class).sourcePath(), typeSpecBuilder);
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenMapper.class;
    }

    @Override
    public String generatePackage(TypeElement typeElement) {
        return typeElement.getAnnotation(GenMapper.class).pkgName();
    }

    private Optional<MethodSpec> dtoToEntityMethod(TypeElement typeElement, DefaultNameContext nameContext) {
        boolean containsNull = StringUtils.containsNull(nameContext.getDtoPackageName());
        if (!containsNull) {
            return Optional.of(MethodSpec
                    .methodBuilder("dtoToEntity")
                    .returns(ClassName.get(typeElement))
                    .addParameter(ClassName.get(nameContext.getDtoPackageName(), nameContext.getDtoClassName()), "dto")
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .build());
        }
        return Optional.empty();
    }

    private Optional<MethodSpec> request2DtoMethod(DefaultNameContext nameContext) {
        boolean containsNull = StringUtils.containsNull(nameContext.getDtoPackageName(), nameContext.getDtoPackageName());
        if (!containsNull) {
            return Optional.of(MethodSpec
                    .methodBuilder("requestToDto")
                    .addParameter(ClassName.get(nameContext.getRequestPackageName(), nameContext.getRequestClassName()), "request")
                    .returns(ClassName.get(nameContext.getDtoPackageName(), nameContext.getDtoClassName()))
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .build());
        }
        return Optional.empty();
    }

    private Optional<MethodSpec> vo2ResponseMethod(DefaultNameContext nameContext) {
        boolean containsNull = StringUtils.containsNull(nameContext.getResponsePackageName(), nameContext.getVoPackageName());
        if (!containsNull) {
            return Optional.of(MethodSpec
                    .methodBuilder("voToResponse")
                    .addParameter(ClassName.get(nameContext.getVoPackageName(), nameContext.getVoClassName()), "vo")
                    .returns(ClassName.get(nameContext.getResponsePackageName(), nameContext.getResponseClassName()))
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .build());
        }
        return Optional.empty();
    }
}
