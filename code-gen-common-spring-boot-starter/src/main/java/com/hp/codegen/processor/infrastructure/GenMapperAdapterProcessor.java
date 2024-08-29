package com.hp.codegen.processor.infrastructure;

import com.google.auto.service.AutoService;
import com.hp.codegen.annotation.domain.GenCustomMapper;
import com.hp.codegen.annotation.domain.GenMapper;
import com.hp.codegen.annotation.infrastructure.GenMapperAdapter;
import com.hp.codegen.annotation.infrastructure.GenPo;
import com.hp.codegen.context.CodeGenContextHolder;
import com.hp.codegen.spi.CodeGenProcessor;
import com.hp.common.base.mapper.GenericDateMapper;
import com.hp.common.base.mapper.GenericEnumMapper;
import com.hp.common.base.mapper.GenericValueObjectMapper;
import com.squareup.javapoet.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.Optional;

/**
 * @author hp
 */
@AutoService(value = CodeGenProcessor.class)
public class GenMapperAdapterProcessor extends AbstractInfrastructureCodeGenProcessor {

    @Override
    protected boolean isInterface() {
        return true;
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenMapperAdapter.class;
    }

    @Override
    protected void customizeType(TypeElement typeElement, RoundEnvironment roundEnvironment, TypeSpec.Builder typeSpecBuilder) {
        final AnnotationSpec mapperAnnotation = AnnotationSpec.builder(Mapper.class)
                .addMember("uses", "$T.class", ClassName.get(GenericEnumMapper.class))
                .addMember("uses", "$T.class", ClassName.get(GenericDateMapper.class))
                .addMember("uses", "$T.class", ClassName.get(GenericValueObjectMapper.class))
                .addMember("uses", "$T.class", CodeGenContextHolder.getClassName(GenCustomMapper.class))
                .build();

        typeSpecBuilder
                .addAnnotation(mapperAnnotation)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(CodeGenContextHolder.getClassName(GenMapper.class))
                .addField(
                        FieldSpec
                                .builder(this.currentGeneratingClassName, "ADAPTER")
                                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                                .initializer(
                                        "$T.getMapper($T.class)",
                                        ClassName.get(Mappers.class),
                                        this.currentGeneratingClassName
                                )
                                .build()
                );

        createEntityToPoMethod().ifPresent(typeSpecBuilder::addMethod);
        createPoToEntityMethod().ifPresent(typeSpecBuilder::addMethod);
    }

    protected boolean methodNotCreatable(String methodName) {
        if (CodeGenContextHolder.missingAnyAnnotated(
                GenPo.class
        )) {
            CodeGenContextHolder.log(
                    Diagnostic.Kind.MANDATORY_WARNING,
                    String.format("To generate %s() on %s requires @GenPo.", methodName, this.currentGeneratingTypeName)
            );
            return true;
        }
        return false;
    }

    protected Optional<MethodSpec> createEntityToPoMethod() {
        final String methodName = "entityToPO";
        if (methodNotCreatable(methodName)) {
            return Optional.empty();
        }
        final MethodSpec methodSpec = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(CodeGenContextHolder.getCurrentTypeClassName(), "entity")
                .returns(CodeGenContextHolder.getClassName(GenPo.class))
                .build();

        return Optional.of(methodSpec);
    }

    protected Optional<MethodSpec> createPoToEntityMethod() {
        final String methodName = "poToEntity";
        if (methodNotCreatable(methodName)) {
            return Optional.empty();
        }
        final MethodSpec methodSpec = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addParameter(CodeGenContextHolder.getClassName(GenPo.class), "po")
                .returns(CodeGenContextHolder.getCurrentTypeClassName())
                .build();

        return Optional.of(methodSpec);
    }
}
