package com.hp.codegen.modifier;

import com.google.common.collect.Lists;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author hp
 */
public abstract class AbstractFieldSpecCreator implements FieldSpecCreator {

    protected final ClassName currentGeneratingClassName;
    protected final Collection<FieldTypeConverter> converters;
    protected final Collection<FieldSpecCustomizer> customizers;

    public AbstractFieldSpecCreator(
            ClassName currentGeneratingClassName,
            Collection<FieldTypeConverter> converters,
            Collection<FieldSpecCustomizer> customizers
    ) {
        this.currentGeneratingClassName = currentGeneratingClassName;
        this.converters = converters;
        this.customizers = customizers;
    }

    @Override
    public List<FieldSpec> createFields(VariableElement fieldVariableElement) {

        final TypeName convertedTypeName = converters.stream()
                .filter(converter -> converter.convertable(fieldVariableElement))
                .findFirst()
                .map(converter -> converter.convert(fieldVariableElement))
                .orElse(TypeName.get(fieldVariableElement.asType()));

        final FieldSpec.Builder builder = FieldSpec.builder(
                convertedTypeName,
                fieldVariableElement.getSimpleName().toString(),
                getModifiers().toArray(new Modifier[0])
        );

        customizers.forEach(t -> t.customizeField(currentGeneratingClassName, fieldVariableElement, convertedTypeName, builder));

        return Collections.singletonList(builder.build());
    }

    protected List<Modifier> getModifiers() {
        return Lists.newArrayList(Modifier.PRIVATE);
    }

}
