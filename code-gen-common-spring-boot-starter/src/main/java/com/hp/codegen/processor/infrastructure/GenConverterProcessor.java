package com.hp.codegen.processor.infrastructure;

import cn.hutool.core.collection.CollUtil;
import com.google.auto.common.MoreTypes;
import com.google.auto.service.AutoService;
import com.hp.codegen.annotation.infrastructure.GenConverter;
import com.hp.codegen.context.CodeGenContextHolder;
import com.hp.codegen.spi.CodeGenProcessor;
import com.hp.codegen.util.CodeGenHelper;
import com.hp.common.base.enums.ValidStatus;
import com.hp.jpa.converter.IntegerBasedBaseEnumTypeConverter;
import com.hp.jpa.converter.StringBasedBaseEnumTypeConverter;
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
public class GenConverterProcessor extends AbstractInfrastructureCodeGenProcessor {

    public static final String ENTITY_CONVERTER_SUFFIX = "Converter";

    public static String getConverterName(VariableElement variableElement) {
        return MoreTypes.asTypeElement(variableElement.asType()).getSimpleName().toString() + ENTITY_CONVERTER_SUFFIX;
    }

    @Override
    protected boolean isInterface() {
        return true;
    }

    @Override
    public Class<? extends Annotation> getAnnotation() {
        return GenConverter.class;
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
                .flatMap(field -> Stream.of(createEntityTypeConverter(field))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                )
                .toList();
        if (CollUtil.isNotEmpty(typeSpecs)) {
            typeSpecBuilder.addTypes(typeSpecs);
        }
    }

    protected Optional<TypeSpec> createEntityTypeConverter(VariableElement field) {
        final TypeSpec.Builder builder = TypeSpec.classBuilder(getConverterName(field))
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);

        final Optional<DeclaredType> baseEnumType = CodeGenHelper.getBaseEnumType(field);
        assert baseEnumType.isPresent();
        final TypeName baseEnumCodeType = CodeGenHelper.getBaseEnumCodeType(baseEnumType.get());

        if (CodeGenContextHolder.isJpaEnvironment()) {
            new JpaConverterSuperClassCustomizer().customize(builder, field, baseEnumCodeType);
            return Optional.of(builder.build());
        }

        if (CodeGenContextHolder.isMyBatisEnvironment()) {
            new MybatisConverterSuperClassCustomizer().customize(builder, field, baseEnumCodeType);
            return Optional.of(builder.build());
        }

        return Optional.empty();
    }

    private record JpaConverterSuperClassCustomizer() {

        private void customize(TypeSpec.Builder builder, VariableElement field, TypeName typeName) {
            if (Objects.equals(typeName, TypeName.get(String.class))) {
                builder.superclass(ParameterizedTypeName.get(ClassName.get(StringBasedBaseEnumTypeConverter.class), TypeName.get(field.asType())));
            }
            if (Objects.equals(typeName, TypeName.get(Integer.class))) {
                builder.superclass(ParameterizedTypeName.get(ClassName.get(IntegerBasedBaseEnumTypeConverter.class), TypeName.get(field.asType())));
            }
        }
    }

    private record MybatisConverterSuperClassCustomizer() {

        private void customize(TypeSpec.Builder builder, VariableElement field, TypeName typeName) {
            if (Objects.equals(typeName, TypeName.get(String.class))) {
                builder.superclass(ParameterizedTypeName.get(ClassName.get(com.hp.mybatisplus.converter.StringBasedBaseEnumTypeConverter.class), TypeName.get(field.asType())));
            }
            if (Objects.equals(typeName, TypeName.get(Integer.class))) {
                builder.superclass(ParameterizedTypeName.get(ClassName.get(com.hp.mybatisplus.converter.IntegerBasedBaseEnumTypeConverter.class), TypeName.get(field.asType())));
            }
        }
    }
}
