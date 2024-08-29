package com.hp.codegen.processor.model;

import cn.hutool.core.collection.CollUtil;
import com.google.auto.common.MoreTypes;
import com.google.auto.service.AutoService;
import com.hp.codegen.annotation.model.GenSerializer;
import com.hp.codegen.context.CodeGenContextHolder;
import com.hp.codegen.spi.CodeGenProcessor;
import com.hp.codegen.util.CodeGenHelper;
import com.hp.common.base.enums.ValidStatus;
import com.hp.common.base.serializer.jackson.IntegerBasedBaseEnumJsonSerializer;
import com.hp.common.base.serializer.jackson.StringBasedBaseEnumJsonSerializer;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author hp
 * @date 2022/10/24
 */
@AutoService(CodeGenProcessor.class)
public class GenSerializerProcessor extends AbstractModelCodeGenProcessor {

    public static final String SERIALIZER_SUFFIX = "Serializer";

    public static String getSerializerName(VariableElement variableElement) {
        return MoreTypes.asTypeElement(variableElement.asType()).getSimpleName().toString() + SERIALIZER_SUFFIX;
    }

    @Override
    protected boolean isInterface() {
        return true;
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenSerializer.class;
    }

    @Override
    protected boolean creatable(TypeElement typeElement, RoundEnvironment roundEnvironment) {
        return CodeGenContextHolder.isJacksonMappingMode();
    }

    @Override
    protected void customizeType(TypeElement typeElement, RoundEnvironment roundEnvironment, TypeSpec.Builder typeSpecBuilder) {

        typeSpecBuilder.addModifiers(Modifier.PUBLIC);

        final List<VariableElement> fields = CodeGenHelper.findNotIgnoreAndNotDeprecatedFields(typeElement);

        final List<VariableElement> baseEnumTypeFields = fields.stream()
                .filter(CodeGenHelper::isBaseEnumType)
                .filter(i -> !Objects.equals(TypeName.get(i.asType()), TypeName.get(ValidStatus.class)))
                .toList();

        if (CollUtil.isEmpty(baseEnumTypeFields)) {
            return;
        }

        final List<TypeSpec> typeSpecs = baseEnumTypeFields.stream()
                .flatMap(field -> Stream.of(createJacksonSerializer(field))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                )
                .toList();
        if (CollUtil.isNotEmpty(typeSpecs)) {
            typeSpecBuilder.addTypes(typeSpecs);
        }
    }

    protected Optional<TypeSpec> createJacksonSerializer(VariableElement field) {
        final TypeSpec.Builder builder = TypeSpec.classBuilder(getSerializerName(field))
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);

        final Optional<DeclaredType> baseEnumType = CodeGenHelper.getBaseEnumType(field);
        assert baseEnumType.isPresent();
        final TypeName baseEnumCodeType = CodeGenHelper.getBaseEnumCodeType(baseEnumType.get());

        if (Objects.equals(baseEnumCodeType, TypeName.get(String.class))) {
            builder.superclass(ParameterizedTypeName.get(ClassName.get(StringBasedBaseEnumJsonSerializer.class), TypeName.get(field.asType())));
            return Optional.of(builder.build());
        }
        if (Objects.equals(baseEnumCodeType, TypeName.get(Integer.class))) {
            builder.superclass(ParameterizedTypeName.get(ClassName.get(IntegerBasedBaseEnumJsonSerializer.class), TypeName.get(field.asType())));
            return Optional.of(builder.build());
        }

        return Optional.empty();
    }

}
