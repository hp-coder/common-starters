package com.hp.codegen.modifier;

import com.google.common.collect.Lists;
import com.hp.codegen.modifier.customizer.DefaultFieldSpecCustomizer;
import com.hp.codegen.modifier.customizer.JacksonBasedFieldSpecCustomizer;
import com.hp.codegen.context.CodeGenContextHolder;
import com.hp.codegen.modifier.converter.BaseEnumNoOpTypeConverter;
import com.hp.codegen.modifier.converter.BaseEnumToCodeTypeTypeConverter;
import com.hp.codegen.modifier.converter.DateTimeToStringFieldTypeConverter;
import com.hp.codegen.modifier.converter.LongToStringFieldTypeConverter;
import com.hp.codegen.util.CodeGenHelper;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import java.util.Collections;
import java.util.List;

/**
 * @author hp
 */
public class FieldSpecCreatorFactory {

    public static FieldSpecCreator createDefaultMapStructMappingFieldSpecCreator(ClassName currentGeneratingClassName) {
        final List<FieldTypeConverter> converters = Lists.newArrayList(
                new LongToStringFieldTypeConverter(),
                new DateTimeToStringFieldTypeConverter(),
                new BaseEnumToCodeTypeTypeConverter()
        );
        converters.addAll(CodeGenContextHolder.getOrm().getFieldTypeConverter());

        final List<FieldSpecCustomizer> customizers = Lists.newArrayList(
                new DefaultFieldSpecCustomizer()
        );
        return new AbstractFieldSpecCreator(currentGeneratingClassName, converters, customizers) {

            @Override
            public List<FieldSpec> createFields(VariableElement fieldVariableElement) {
                final List<FieldSpec> fields = super.createFields(fieldVariableElement);

                if (!CodeGenHelper.isBaseEnumType(fieldVariableElement)) {
                    return fields;
                }

                final FieldSpec fieldSpec = FieldSpec.builder(
                        ClassName.get(String.class),
                        fieldVariableElement.getSimpleName().toString() + "Name",
                        getModifiers().toArray(new Modifier[0])
                ).build();

                final List<FieldSpec> fieldSpecs = Lists.newArrayList(fields);
                fieldSpecs.add(fieldSpec);
                return fieldSpecs;
            }
        };
    }

    public static FieldSpecCreator createDefaultEmptyConverterFieldSpecCreator(ClassName currentGeneratingClassName) {
        final List<FieldSpecCustomizer> customizers = Lists.newArrayList(
                new DefaultFieldSpecCustomizer()
        );
        return new AbstractFieldSpecCreator(currentGeneratingClassName, Collections.emptyList(), customizers) {
        };
    }

    public static FieldSpecCreator createDefaultOrmConverterFieldSpecCreator(ClassName currentGeneratingClassName) {
        final List<FieldSpecCustomizer> customizers = Lists.newArrayList(
                new DefaultFieldSpecCustomizer()
        );
        return new AbstractFieldSpecCreator(currentGeneratingClassName, CodeGenContextHolder.getOrm().getFieldTypeConverter(), customizers) {
        };
    }

    public static FieldSpecCreator createDefaultJacksonSerializerMappingFieldSpecCreator(ClassName currentGeneratingClassName) {
        final List<FieldTypeConverter> converters = Lists.newArrayList(
                new LongToStringFieldTypeConverter(),
                new DateTimeToStringFieldTypeConverter(),
                new BaseEnumNoOpTypeConverter()
        );
        converters.addAll(CodeGenContextHolder.getOrm().getFieldTypeConverter());

        final List<FieldSpecCustomizer> customizers = Lists.newArrayList(
                new DefaultFieldSpecCustomizer(),
                new JacksonBasedFieldSpecCustomizer()
        );

        return new AbstractFieldSpecCreator(currentGeneratingClassName, converters, customizers) {
        };
    }

    public static FieldSpecCreator createDefaultJacksonDeserializerMappingFieldSpecCreator(ClassName currentGeneratingClassName) {
        final List<FieldTypeConverter> converters = Lists.newArrayList(
                new BaseEnumNoOpTypeConverter()
        );
        converters.addAll(CodeGenContextHolder.getOrm().getFieldTypeConverter());

        final List<FieldSpecCustomizer> customizers = Lists.newArrayList(
                new DefaultFieldSpecCustomizer(),
                new JacksonBasedFieldSpecCustomizer()
        );

        return new AbstractFieldSpecCreator(currentGeneratingClassName, converters, customizers) {
        };
    }
}
