package com.hp.codegen.processor.domain;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.google.auto.service.AutoService;
import com.google.common.collect.Lists;
import com.hp.codegen.annotation.domain.GenCustomMapper;
import com.hp.codegen.spi.CodeGenProcessor;
import com.hp.codegen.util.CodeGenHelper;
import com.hp.common.base.enums.ValidStatus;
import com.squareup.javapoet.*;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author hp
 */
@AutoService(value = CodeGenProcessor.class)
public class GenCustomMapperProcessor extends AbstractDomainCodeGenProcessor {

    @Override
    protected boolean isInterface() {
        return true;
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenCustomMapper.class;
    }

    @Override
    protected void customizeType(TypeElement typeElement, RoundEnvironment roundEnvironment, TypeSpec.Builder typeSpecBuilder) {
        typeSpecBuilder
                .addModifiers(Modifier.PUBLIC);

        final List<VariableElement> fields = CodeGenHelper.findNotIgnoreAndNotDeprecatedFields(typeElement);

        final List<VariableElement> baseEnumTypeFields = fields.stream()
                .filter(CodeGenHelper::isBaseEnumType)
                .filter(i -> !Objects.equals(TypeName.get(i.asType()), TypeName.get(ValidStatus.class)))
                .toList();

        if (CollUtil.isEmpty(baseEnumTypeFields)) {
            return;
        }

        final List<MethodSpec> methodSpecs = baseEnumTypeFields.stream()
                .flatMap(field -> createBaseEnumCustomMapperMethods(field).stream())
                .toList();
        if (CollUtil.isNotEmpty(methodSpecs)) {
            typeSpecBuilder.addMethods(methodSpecs);
        }
    }

    private List<MethodSpec> createBaseEnumCustomMapperMethods(VariableElement field) {
        final Optional<DeclaredType> baseEnumTypeOpt = CodeGenHelper.getBaseEnumType(field);
        assert baseEnumTypeOpt.isPresent();

        final ClassName baseEnumType = (ClassName) CodeGenHelper.getBaseEnumType(baseEnumTypeOpt.get());
        final ClassName baseEnumCodeType = (ClassName) CodeGenHelper.getBaseEnumCodeType(baseEnumTypeOpt.get());

        final MethodSpec codeToBaseEnumMethod = MethodSpec.methodBuilder(StrUtil.lowerFirst(baseEnumCodeType.simpleName()) + "To" + baseEnumType.simpleName())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ParameterSpec.builder(baseEnumCodeType, "code").build())
                .addCode(
                        CodeBlock.of(
                                """
                                         return $T.of(code).orElse(null);
                                        """,
                                baseEnumType
                        )
                )
                .returns(baseEnumType)
                .build();

        final MethodSpec baseEnumToCodeMethod = MethodSpec.methodBuilder(StrUtil.lowerFirst(baseEnumType.simpleName()) + "To" + baseEnumCodeType.simpleName())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ParameterSpec.builder(baseEnumType, "baseEnum").build())
                .addCode(
                        CodeBlock.of(
                                """
                                         return $T.ofNullable(baseEnum).map($T::getCode).orElse(null);
                                        """,
                                ClassName.get(Optional.class),
                                baseEnumType
                        )
                )
                .returns(baseEnumCodeType)
                .build();

        return Lists.newArrayList(codeToBaseEnumMethod, baseEnumToCodeMethod);
    }
}
