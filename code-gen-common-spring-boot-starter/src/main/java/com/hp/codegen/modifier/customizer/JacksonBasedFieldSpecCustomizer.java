package com.hp.codegen.modifier.customizer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hp.codegen.annotation.model.GenDeserializer;
import com.hp.codegen.annotation.model.GenSerializer;
import com.hp.codegen.processor.model.GenDeserializerProcessor;
import com.hp.codegen.processor.model.GenSerializerProcessor;
import com.hp.codegen.context.CodeGenContextHolder;
import com.hp.codegen.modifier.FieldSpecCustomizer;
import com.hp.codegen.util.CodeGenHelper;
import com.hp.common.base.deserializer.jackson.ValidStatusDeserializer;
import com.hp.common.base.enums.ValidStatus;
import com.hp.common.base.serializer.jackson.ValidStatusSerializer;
import com.squareup.javapoet.*;

import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import java.util.Objects;
import java.util.Optional;

/**
 * @author hp
 */
public class JacksonBasedFieldSpecCustomizer implements FieldSpecCustomizer {

    @Override
    public void customizeField(ClassName currentGeneratingClassName, VariableElement fieldVariableElement, TypeName convertedFieldTypeName, FieldSpec.Builder fieldSpecBuilder) {
        CodeGenContextHolder.log(
                Diagnostic.Kind.MANDATORY_WARNING,
                "currentGeneratingClassName.simpleName()=" + currentGeneratingClassName.simpleName()
        );
        final boolean isRequest = currentGeneratingClassName.simpleName().endsWith("Request");
        if (isRequest) {
            usingDeserializerCodeBlock(fieldVariableElement, convertedFieldTypeName)
                    .ifPresent(codeBlock ->
                            fieldSpecBuilder.addAnnotation(
                                    AnnotationSpec.builder(JsonDeserialize.class)
                                            .addMember(
                                                    "using",
                                                    codeBlock
                                            )
                                            .build()
                            )
                    );

        }

        final boolean isResponse = currentGeneratingClassName.simpleName().endsWith("Response");
        if (isResponse) {
            usingSerializerCodeBlock(fieldVariableElement, convertedFieldTypeName)
                    .ifPresent(codeBlock ->
                            fieldSpecBuilder.addAnnotation(
                                    AnnotationSpec.builder(JsonSerialize.class)
                                            .addMember(
                                                    "using",
                                                    codeBlock
                                            )
                                            .build()
                            )
                    );
        }
    }

    protected Optional<CodeBlock> usingSerializerCodeBlock(VariableElement variableElement, TypeName typeName) {
        if (Objects.equals(typeName, TypeName.get(ValidStatus.class))) {
            final CodeBlock codeBlock = CodeBlock.of(
                    "$T.class",
                    ClassName.get(ValidStatusSerializer.class)
            );
            return Optional.of(codeBlock);
        } else {
            if (!CodeGenHelper.isBaseEnumType(variableElement)) {
                return Optional.empty();
            }
            final CodeBlock codeBlock = CodeBlock.of(
                    "$T.class",
                    ClassName.get(
                            CodeGenContextHolder.getPackageName(GenSerializer.class),
                            CodeGenContextHolder.getClassSimpleName(GenSerializer.class),
                            GenSerializerProcessor.getSerializerName(variableElement)
                    )
            );
            return Optional.of(codeBlock);
        }
    }

    protected Optional<CodeBlock> usingDeserializerCodeBlock(VariableElement variableElement, TypeName typeName) {
        if (Objects.equals(typeName, TypeName.get(ValidStatus.class))) {
            final CodeBlock codeBlock = CodeBlock.of(
                    "$T.class",
                    ClassName.get(ValidStatusDeserializer.class)
            );
            return Optional.of(codeBlock);
        } else {
            if (!CodeGenHelper.isBaseEnumType(variableElement)) {
                return Optional.empty();
            }
            final CodeBlock codeBlock = CodeBlock.of(
                    "$T.class",
                    ClassName.get(
                            CodeGenContextHolder.getPackageName(GenDeserializer.class),
                            CodeGenContextHolder.getClassSimpleName(GenDeserializer.class),
                            GenDeserializerProcessor.getDeserializerName(variableElement)
                    )
            );
            return Optional.of(codeBlock);
        }
    }
}
